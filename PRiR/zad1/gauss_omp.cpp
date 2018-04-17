#include <iostream>
#include <omp.h>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace std;
using namespace cv;

const int mask[5][5] = {
        {0,1,2,1,0},
        {1,4,8,4,1},
        {2,8,16,8,2},
        {1,4,8,4,1},
        {0,1,2,1,0}
};

Mat gaussianBlur(Mat inputImage, Mat outputImage, int threads, int weight) {
    int y, x, red, green, blue;
    #pragma omp parallel for shared(inputImage, outputImage, weight) num_threads(threads) private(red,green,blue,y,x) schedule(static)
    for (y = 2; y < inputImage.rows - 2; y++) {
        for (x = 2; x < inputImage.cols - 2; x++) {
            red = 0; green = 0; blue = 0;
            for (int y_m = 0; y_m<5; y_m++) {
                for (int x_m = 0; x_m<5; x_m++) {
                    Vec3b intensity = inputImage.at <Vec3b>(y_m + y - 2, x_m + x - 2);

                    red += intensity.val[2] * mask[x_m][y_m];
                    green += intensity.val[1] * mask[x_m][y_m];
                    blue += intensity.val[0] * mask[x_m][y_m];
                }
            }
            Vec3b masks = Vec3b();
            masks.val[2] = red / weight;
            masks.val[1] = green / weight;
            masks.val[0] = blue / weight;
            outputImage.at<Vec3b>(y - 2, x - 2) = masks;
        }
    }

    return outputImage;
}

int main(int argc, char** argv )
{
    int threads;
    double startTime, execTime;
    Mat inputImage, outputImage;
    string inputName, outputName;
    int weight = 0;

    if (argc < 4)
    {
        cout << "Invalid arguments" << endl;
        return -1;
    }
    else {
        inputName= argv[2];
        outputName= argv[3];
        threads = atoi(argv[1]);
    }

    if (threads <= 0){
        cout << "Not enough threads" << endl;
        return -1;
    }

    inputImage = imread(inputName, CV_LOAD_IMAGE_COLOR);
    if (inputImage.data == false)
    {
        cout << "No image defined" << endl;
        return -1;
    }

    outputImage = inputImage.clone();

    for (int x = 0; x<5; x++) {
        for (int y = 0; y<5; y++) {
            weight += mask[x][y];
        }
    }

    copyMakeBorder(inputImage, inputImage, 2, 2, 2, 2, BORDER_REPLICATE);

    startTime = omp_get_wtime();

    outputImage = gaussianBlur(inputImage, outputImage, threads, weight);

    execTime = 1000 * (omp_get_wtime() - startTime);

    cout << "Czas: " << execTime << "ms" << endl;

    try {
        imwrite(outputName, outputImage);
    }
    catch (Exception &e) {
        cout << "Exception while writing to file: \n" << e.msg;
        return 0;
    }

    return 0;
}