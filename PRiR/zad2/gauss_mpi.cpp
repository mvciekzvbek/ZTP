#include <iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include "mpi.h"

using namespace std;
using namespace cv;

MPI_Comm comm;

const int mask[5][5] = {
        {0,1,2,1,0},
        {1,4,8,4,1},
        {2,8,16,8,2},
        {1,4,8,4,1},
        {0,1,2,1,0}
};

Mat gaussianBlur(Mat inputImage, Mat outputImage, int weight) {
    int y, x, red, green, blue;
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

void send(void * data, int count, MPI_Datatype datatype = MPI_LONG, int dest = 0, int tag = 0) {
    MPI_Send(data, count, datatype, dest, tag, comm);
}

void receive(void * data, int count, MPI_Datatype datatype = MPI_LONG, int src = 0, int tag = 0) {
    MPI_Recv(data, count, datatype, src, tag, comm, MPI_STATUS_IGNORE);
}

int main (int argc, char* argv[])
{
    int processes, processId;
    Mat inputImage, outputImage, imagePart;
    string inputName, outputName;
    double startTime;
    int weight = 0;

    MPI_Init(&argc, &argv);
    comm = MPI_COMM_WORLD;
    MPI_Comm_rank(comm, &processId);
    MPI_Comm_size(comm, &processes);

    for (int xMask = 0; xMask<5; xMask++) {
        for (int yMask = 0; yMask<5; yMask++) {
            weight += mask[xMask][yMask];
        }
    }

    if (processId == 0) {
        //argument validation
        if (argc < 3) {
            cout << "Invalid arguments" << endl;
            MPI_Finalize();
            return -1;
        } else {
            inputName = argv[1];
            outputName = argv[2];
        }

        if (processes <= 0) {
            cout << "Not enough processes" << endl;
            MPI_Finalize();
            return -1;
        }

        inputImage = imread(inputName, CV_LOAD_IMAGE_COLOR);

        if (inputImage.data == false) {
            cout << "No image defined" << endl;
            MPI_Finalize();
            return -1;
        } else {
            if (inputImage.cols < processes) {
                cout << "Image is to small for " << processes <<" processes!";
                MPI_Finalize();
                return -1;
            }
        }

        copyMakeBorder(inputImage, inputImage, 2, 2, 2, 2, BORDER_REPLICATE);

        startTime = MPI_Wtime();

        for (int i = processes-1; i > -1; i--) {

            int xPoint = i*(inputImage.cols - 4) / processes;
            int xWidth = (i+1)*(inputImage.cols - 4) / processes - xPoint + 4;

            //divide image
            imagePart = Mat(xWidth, inputImage.rows, CV_8UC3);
            imagePart = inputImage(Rect(xPoint, 0, xWidth, inputImage.rows)).clone();

            int x = imagePart.cols;
            int y = imagePart.rows;

            //send part
            if (i != 0) {
                send(&x, sizeof(int), MPI_LONG, i, 0);
                send(&y, sizeof(int), MPI_LONG, i, 1);
                send(imagePart.data, x*y * 3, MPI_BYTE, i, 2);
            }
        }
    } else {
        // other processes receive
        int x,y;
        receive(&x, sizeof(int), MPI_LONG, 0, 0);
        receive(&y, sizeof(int), MPI_LONG, 0, 1);

        imagePart = Mat(y, x, CV_8UC3);
        receive(imagePart.data, x*y*3, MPI_BYTE, 0, 2);
    }

    Mat copyToBlur = imagePart.clone();

    copyToBlur = copyToBlur(Rect(2, 2, copyToBlur.cols - 4, copyToBlur.rows - 4)).clone();
    copyToBlur = gaussianBlur(imagePart, copyToBlur, weight);

    if (processId != 0) {
        int bluuredColumns = copyToBlur.cols;
        int bluuredRows = copyToBlur.rows;


        send(&bluuredColumns, sizeof(int), MPI_LONG, 0, 0);
        send(&bluuredRows, sizeof(int), MPI_LONG, 0, 1);
        send(copyToBlur.data, bluuredColumns * bluuredRows * 3, MPI_BYTE, 0, 2);
    } else {
        for (int i = 1; i < processes; i++) {
            int tempCols, tempRows;
            receive(&tempCols, sizeof(int), MPI_LONG, i, 0);
            receive(&tempRows, sizeof(int), MPI_LONG, i, 1);

            Mat tempImage = Mat(tempRows, tempCols, CV_8UC3);

            receive(tempImage.data, tempCols*tempRows * 3, MPI_BYTE, i, 2);

            hconcat(copyToBlur, tempImage, copyToBlur);
        }

        double execTime = 1000 * (MPI_Wtime() - startTime);

        cout << "Czas: " << execTime << "ms" << endl;

        try {
            imwrite(outputName, copyToBlur);
        } catch (Exception &e) {
            cout << "Exception while writing to file: \n" << e.msg;
            MPI_Finalize();
            return -1;
        }
    }

    MPI_Finalize();

    return 0;
}