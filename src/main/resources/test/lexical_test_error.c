/* HelloWorld.c */
//结构体声明语句
struct Time{
    int year,month,day;
    char timeZone;
} time;

int test(int s); //过程声明语句

int main(){
    int x=3*2+9/3; //简单变量声明语句
    double y=5.2e-1; //浮点数 科学计数法
    int z=0127; //八进制数
    int w=0xABC; //十六进制数
	double* yptr=&y; //指针声明语句

    int array[5][5]; //(高维)数组声明语句
    array[3][4]=5; //(高维)数组赋值

    printf("Hello,World!%d",test(x)); //字符串常数、过程调用语句
    printf("array[3][4]=%d",array[3][4]); //(高维)数组引用
    return 0;
}

int test(int s){
    if(s>5 && s<=10){ //关系表达式、逻辑表达式、分支语句
        s=(s+1)*10; //算术表达式
    }
    int i=0;@
	do{			//循环语句
		$s=s+i;
		i=i+1;
	}while(i<5);
    return s;
}

/*注释未封闭
int test2(int x){
	return x+1;
}