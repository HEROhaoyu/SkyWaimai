package com.sky.exception;

public class AddressBookBusinessException extends BaseException {//这句话的意思是AddressBookBusinessException继承BaseException，作用是：AddressBookBusinessException可以使用BaseException的方法，BaseException用于处理异常

    public AddressBookBusinessException(String msg) {//这句话的意思是AddressBookBusinessException的构造方法，作用是：当AddressBookBusinessException被实例化时，可以传入一个字符串，这个字符串是异常信息
        super(msg);//这句话的意思是调用父类的构造方法，作用是：将传入的字符串传给父类的构造方法
    }

}
