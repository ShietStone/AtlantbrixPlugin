package org.abp.systems;

import org.abp.data.DataBase;
import org.abp.logging.Logger;

public interface ApplicationSystem {

	public void load(Logger logger, DataBase dataBase);
	public void save(Logger logger, DataBase dataBase);
}