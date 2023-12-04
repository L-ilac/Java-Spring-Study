// CG_Midterm.cpp : This file contains the 'main' function. Program execution begins and ends there.
#define _USE_MATH_DEFINES
#include <vector>
#include <math.h>
#include <GL/glut.h>
#include <GL/GL.h>
#include <GL/GLU.h>

//점을 저장하기 위한 class
class xPoint3D
{
public:
    GLfloat x, y, z;
    xPoint3D()
    {
        x = 0.0;
        y = 0.0;
        z = 0.0;
    };
};

GLfloat currentWidth = 600.0; // 현재의 창 크기를 저장하는 변수
GLfloat currentHeight = 600.0;
GLfloat initWidth = 600.0; // 처음 창 크기를 저장하는 변수
GLfloat initHeight = 600.0;

std::vector<xPoint3D> arPoints;
std::vector<xPoint3D> arInputPoints;
std::vector<xPoint3D> arRotPoints;

// 전체 점의 갯수를 저장하는 변수
GLint count = 0;

void init(void)
{
    //초기화 색
    glClearColor(0.3, 0.4, 0.5, 0.0);

    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    //뷰 볼륨 설정
    glOrtho(-1.0, 1.0, -1.0, 1.0, -1.0, 1.0);
}

void MyDisplay(void)
{
    //디스플레이 콜백 함수

    GLint index;
    glClear(GL_COLOR_BUFFER_BIT);
    //입력 기본요소 정의
    xPoint3D pt;
    glColor3f(1, 0, 0);

    if (count > 0)
        for (int i = 0; i < count; i++)
        {
            glBegin(GL_POINTS);
            glVertex3f(arPoints[i].x, arPoints[i].y, arPoints[i].z);
            // glVertex3f(pt.x,pt.y,pt.z);
        }
    glEnd();

    glColor3f(1, 0, 0);
    glBegin(GL_LINES);
    glVertex3f(-1.0, 0.0, 0.0);
    glVertex3f(1.0, 0.0, 0.0);
    glEnd();

    glColor3f(0, 1, 0);
    glBegin(GL_LINES);
    glVertex3f(0.0, -1.0, 0.0);
    glVertex3f(0.0, 1.0, 0.0);
    glEnd();

    glFlush();

    // point color
    glColor3f(1.0, 0.0, 0.0);
    // Set point size
    glPointSize(6.0);
}

// 다시 그려주는 함수
void MyReshape(int NewWidth, int NewHeight)
{
    // 창 크기가 바뀔 때 새로운 창의 크기를 저장
    currentWidth = NewWidth;
    currentHeight = NewHeight;

    // 뷰포트 설정
    glViewport(0, 0, NewWidth, NewHeight);
    glLoadIdentity();
    glOrtho(-1.0, 1.0, -1.0, 1.0, -1.0, 1.0); //직교 투영을 하겠다.
}

//점 찍기
void plotPoint(GLint x, GLint y, GLint z)
{
    GLint index;
    xPoint3D pt;

    glPointSize(6.0);

    if (count > 0)
    {
        for (int i = 0; i < count; i++)
        {
            glBegin(GL_POINTS);
            glVertex3f(arPoints[i].x, arPoints[i].y, arPoints[i].z);

            printf("%d\n", i);
            // glVertex3f(pt.x, pt.y, pt.z);
        }
    }
    glEnd();
    glFlush();
}

// 마우스 클릭 이벤트 Call Back 함수
void MyMouseClick(GLint Button, GLint State, GLint xMouse, GLint yMouse)
{
    // 마우스의 왼쪽버튼이 클릭되었을 때
    if (Button == GLUT_LEFT_BUTTON && State == GLUT_DOWN)
    {
        xPoint3D pt;
        pt.x = (2 * xMouse / currentWidth) - 1;
        pt.y = -(2 * yMouse / currentHeight) + 1;
        pt.z = 0;
        arPoints.push_back(pt);

        count++; // 전체 좌표의 수를 증가

        plotPoint(pt.x, pt.y, pt.z);
        printf("(%d,%d,%d)\n", pt.x, pt.y, pt.z);
    }
}

// Y축 회전

// Y축 회전
void YrotPlotPoint(GLint x, GLint y, GLint z)
{
    GLint index;
    xPoint3D pt;

    glPointSize(6.0);

    if (count > 0)
    {
        for (int i = 0; i < count; i++)
        {
            glBegin(GL_POINTS);
            glVertex3f(arRotPoints[i].x, arRotPoints[i].y, arRotPoints[i].z);

            printf("%d\n", i);
            // glVertex3f(pt.x, pt.y, pt.z);
        }
    }
    glEnd();
    glFlush();
}

void rotY()
{

    float radian = 30 * (M_PI / 180);

    for (int i = 0; i < count; i++)
    {
        xPoint3D newPt;
        newPt.x = arPoints[i].z * sin(radian) + arPoints[i].y * cos(radian);
        newPt.y = arPoints[i].y;
        newPt.z = arPoints[i].z * cos(radian) - arPoints[i].x * sin(radian);

        arRotPoints.push_back(newPt);
        printf("rotY 실행");
        printf("(%d,%d,%d)\n", newPt.x, newPt.y, newPt.z);

        YrotPlotPoint(newPt.x, newPt.y, newPt.z);
    }
}

//메인 메뉴 Exit
void MyMainMenu(int entryID)
{
    switch (entryID)
    {
    case 1:
        glClear(GL_COLOR_BUFFER_BIT); // Clear display window.
        break;
    case 2:
        rotY();
        break;
    case 3:
        exit(0);
        break;
    }

    glutPostRedisplay();
}

int main(int argc, char **argv)
{
    // 메인 윈도우 초기화
    glutInit(&argc, argv);
    glutInitDisplayMode(GLUT_RGB);
    glutInitWindowSize(currentWidth, currentHeight);
    glutInitWindowPosition(0, 0);
    glutCreateWindow("");
    glClearColor(1.0, 1.0, 1.0, 1.0);

    //메뉴
    GLint MyMainMenuID = glutCreateMenu(MyMainMenu);
    glutAddMenuEntry("clear", 1);
    glutAddMenuEntry("rotate30", 2);
    glutAddMenuEntry("Exit", 0);
    glutAttachMenu(GLUT_RIGHT_BUTTON);

    glClear(GL_COLOR_BUFFER_BIT);

    init();

    // Call Back 함수 등록
    glutDisplayFunc(MyDisplay);
    glutReshapeFunc(MyReshape);
    glutMouseFunc(MyMouseClick);
    glutMainLoop();
    return 0;
}