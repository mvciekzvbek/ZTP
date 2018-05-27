
#include "cuda_runtime.h"
#include "device_launch_parameters.h"
#include <iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#define HANDLE_ERROR(err) (HandleError(err, __FILE__, __LINE__))

using namespace std;
using namespace cv;

static const int MASK_SIZE = 5;
static const int mask[MASK_SIZE][MASK_SIZE] = {
        {0,1,2,1,0},
        {1,4,8,4,1},
        {2,8,16,8,2},
        {1,4,8,4,1},
        {0,1,2,1,0}
};

static void HandleError(cudaError_t err, string file, int line)
{
    if (err != cudaSuccess)
    {
        cout << cudaGetErrorString(err) << " in " << file << " at line " << line << endl;
        exit(EXIT_FAILURE);
    }
}

__constant__ int dev_mask[MASK_SIZE][MASK_SIZE];
__constant__ int dev_weight;

__global__ void gaussianBlur(uchar * inputImage, uchar * outputImage, long width, long height)
{
    long x = (blockIdx.x * blockDim.x) + threadIdx.x;
    long y = (blockIdx.y * blockDim.y) + threadIdx.y;

    if (x < width-2 && y < height-2 && x>1 && y>1)
    {
        long r=0, g=0, b=0;
        long pixelIn, pixelOut;

        for (int y_m = 0; y_m<5; y_m++)
        {
            for (int x_m = 0; x_m<5; x_m++)
            {
                pixelIn = width*(y + y_m - 2) * 3 + (x + x_m - 2) * 3;

                r += inputImage[pixelIn + 2] * dev_mask[x_m][y_m];
                g += inputImage[pixelIn + 1] * dev_mask[x_m][y_m];
                b += inputImage[pixelIn] * dev_mask[x_m][y_m];
            }
        }
        pixelOut = (width - 4)*(y - 2) * 3 + (x - 2) * 3;
        outputImage[pixelOut + 2] = r / dev_weight;
        outputImage[pixelOut + 1] = g / dev_weight;
        outputImage[pixelOut] = b / dev_weight;
    }
}

void compute(int blockSize, Mat inputImage, string inputName, string outputName) {
    Mat outputImage;
    outputImage = Mat(inputImage.rows, inputImage.cols, CV_8UC3);
    copyMakeBorder(inputImage, inputImage, 2, 2, 2, 2, BORDER_REPLICATE);

    int gridWidth, gridHeight;
    gridWidth = inputImage.cols / blockSize + ((inputImage.cols % blockSize) == 0 ? 0 : 1);
    gridHeight = inputImage.rows / blockSize + ((inputImage.rows % blockSize) == 0 ? 0 : 1);

    long inputSize = sizeof(uchar) * inputImage.rows* inputImage.cols * 3;
    long outputSize = sizeof(uchar) * outputImage.rows* outputImage.cols * 3;

    uchar * dev_inputImage;
    uchar * dev_outputImage;
    HANDLE_ERROR(cudaMalloc((void**)& dev_inputImage, inputSize));
    HANDLE_ERROR(cudaMalloc((void**)& dev_outputImage, outputSize));

    HANDLE_ERROR(cudaMemcpy(dev_inputImage, inputImage.data, inputSize, cudaMemcpyHostToDevice));

    dim3 gridSize(gridWidth, gridHeight);
    dim3 threadsPerBlock(blockSize, blockSize);

    cudaEvent_t begin, end;
    float time;
    HANDLE_ERROR(cudaEventCreate(&begin));
    HANDLE_ERROR(cudaEventCreate(&end));
    HANDLE_ERROR(cudaEventRecord(begin, 0));

    gaussianBlur <<< gridSize,threadsPerBlock >>> (dev_inputImage, dev_outputImage, inputImage.cols, inputImage.rows);

    HANDLE_ERROR(cudaEventRecord(end, 0));
    HANDLE_ERROR(cudaEventSynchronize(end));
    HANDLE_ERROR(cudaEventElapsedTime(&time, begin, end));

    HANDLE_ERROR(cudaMemcpy(outputImage.data, dev_outputImage, outputSize, cudaMemcpyDeviceToHost));

    try {
        imwrite(outputName, outputImage);
    }
    catch (Exception &e) {
        cout << "Exception while writing to file " << e.msg;
    }

    HANDLE_ERROR(cudaEventDestroy(begin));
    HANDLE_ERROR(cudaEventDestroy(end));

    cudaFree(dev_inputImage);
    cudaFree(dev_outputImage);

//    cout << "X: "<< gridWidth << ", Y: " << gridHeight << endl;
//    cout << "Threads per block: " << blockSize << endl;
    cout << "Czas: " << time << "ms" << endl;
}

int main(int argc, char** argv) {
    Mat inputImage;
    string inputName, outputName;
    int maxThreads = 32;
    int blockSize = 32;
    int weight = 0;

    if (argc < 3)
    {
        cout << "Invalid arugments";
        return -1;
    }
    else
    {
        inputName = argv[1];
        outputName = argv[2];
        if (argc == 4)
        {
            blockSize = atoi(argv[3]);
        }
    }

    inputImage = imread(inputName, CV_LOAD_IMAGE_COLOR);
    if (inputImage.data == false)
    {
        cout << "No image defined";
        return -1;
    }

    for (int xMask = 0; xMask<5; xMask++) {
        for (int yMask = 0; yMask<5; yMask++) {
            weight += mask[xMask][yMask];
        }
    }

    HANDLE_ERROR(cudaMemcpyToSymbol(dev_mask, &mask, sizeof(int) * MASK_SIZE * MASK_SIZE));
    HANDLE_ERROR(cudaMemcpyToSymbol(dev_weight, &weight, sizeof(int)));

    struct cudaDeviceProp properties;
    HANDLE_ERROR(cudaGetDeviceProperties(&properties, 0));

    if (blockSize <= maxThreads && blockSize > 0)
    {
        compute(blockSize, inputImage, inputName, outputName);
    }
    else
    {
        for(int size=1; size <= maxThreads; size++)
        {
            compute(size, inputImage, inputName, outputName);
        }
    }

    return 0;
}
