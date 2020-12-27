package org.abp.logging;

public class MultiLogger implements Logger {

	private Logger[] loggers;
	
	public MultiLogger(Logger[] loggers) {
		this.loggers = loggers;
	}
	
	@Override
	public void log(String message) {
		if(loggers != null)
			for(Logger logger : loggers)
				if(logger != null)
					logger.log(message);
	}
}
