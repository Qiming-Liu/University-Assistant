#include <iostream>
#include <windows.h>
#include <string>
#include <ctime>

int main()
{
	//c++控制台小程序，用来方便启动服务器
	HWND hwnd = ::GetConsoleWindow();
	SendMessage(hwnd, WM_SYSCOMMAND, SC_MAXIMIZE, 0);

	time_t t = time(NULL);
	char ch[64] = { 0 };
	strftime(ch, sizeof(ch) - 1, "%Y-%m-%d %H:%M:%S", localtime(&t));
	std::string time = ch;
	std::string log = time + ".txt";
	std::string cmd = "java -jar ./YZS-2.1.2.RELEASE.jar > " + log + " && type " + log;
	system(cmd.c_str());
}
