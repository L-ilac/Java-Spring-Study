#include <stdio.h>

class xPoint3D
{
public:
    float x, y, z;
    xPoint3D()
    {
        x = 0.0;
        y = 0.0;
        z = 0.0;
    };
};
float currentWidth = 600.0;
float currentHeight = 600.0;

int main(int argc, char const *argv[])
{
    /* code */
    int xMouse = 300;
    int yMouse = 200;

    xPoint3D pt;

    pt.x = (2 * xMouse / currentWidth) - 1;
    pt.y = -(2 * yMouse / currentHeight) + 1;
    pt.z = 0;

    printf("(%f,%f,%f)\n", pt.x, pt.y, pt.z);

    return 0;
}
