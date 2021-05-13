/* HelloWorld.c */
//结构体声明语句
struct Time{
	int year;
	int month;
	int day;
	char timeZone;
};

function int test(int s,int t); //过程声明语句

function int test(int y);//过程重复声明

function int main(){
	int x;
	double y;
	int z;
	int w;
	int array[5][5]; //(高维)数组声明语句
	int y;//变量重复声明
	struct Time time;
	
	x=3*2+9/3; //简单变量赋值语句
	y=5.2e-1; //浮点数 科学计数法
	z=0127; //八进制数
	w=0xABC; //十六进制数
	array[3][4]=5; //(高维)数组赋值
	w=x+z+array[1][3];
	
	a=12;//变量未经声明就使用

	time.year=2021;
	time.month=5;
	time.year=12;
	x.year=1;//对非结构体类型变量使用“.”操作符
	x[1]=2;//对非数组变量使用数组访问操作符
	
	call test(5,w); //函数调用
	call test2();//函数未经声明就使用
	call time();//对非过程名使用过程调用操作符
	
	call test(3);
	call test(y,time);//过程调用的参数类型或数目不匹配
	
	z=array[1.0][3.2];//数组下标不是整数
	x=time*z;//运算分量类型不匹配 结构体与整型数字相加
	
	return 0;
}

function int test(int s,int t){
	int i;
	double j;
	i=0;
	if(s>5 && s<=10){ //关系表达式、逻辑表达式、分支语句
		s=(s+1)*10; //算术表达式
		t=t+1;
	}else{
		s=(s-1)/10;
		t=t-1;
	}
	
	do{			//循环语句
		s=s+i;
		i=i+1;
	}while(i<5);
	
	return j;
}