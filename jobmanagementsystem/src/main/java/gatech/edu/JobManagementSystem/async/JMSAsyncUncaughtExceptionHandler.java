package gatech.edu.JobManagementSystem.async;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class JMSAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler{

	Logger logger = LoggerFactory.getLogger(JMSAsyncUncaughtExceptionHandler.class);
	
	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		// TODO Auto-generated method stub
		logger.error("Caught an uncaught exception in an async thread");
		logger.error("Throwable:"+ex.getMessage());
		logger.error("Method:"+method.getName());
		for(Object param:params) {
			logger.error("Param:"+param.toString());
		}
	}
}