/* HelloWorld.c */
//结构体声明语句
struct Time{
	int year;
	int month;
	int day;
	char timeZone;
};

function int test(int s,int t); //过程声明语句

function int main(){
	int x;
	double y;
	int z;
	int w;
	int array[5][5]; //(高维)数组声明语句
	struct Time time;//结构体变量声明语句
	
	x=3*2+9/3; //简单变量赋值语句
	y=5.2e-1; //浮点数 科学计数法
	z=0127; //八进制数
	w=0xABC; //十六进制数
	array[3][4]=5; //(高维)数组赋值
	w=x+z+array[1][3];
	
	time.year=2021;
	time.month=5;
	time.year=12;
	
	call test(5,w); //函数调用
	
	return 0;
}

function int test(int s,int t){
	int i;
	
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
	
	return s;
}