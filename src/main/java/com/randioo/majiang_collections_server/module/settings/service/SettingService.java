package com.randioo.majiang_collections_server.module.settings.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface SettingService extends ObserveBaseServiceInterface {

	GeneratedMessage saveSettings(Role role, int volume, int musicVolume);
	
	GeneratedMessage getSettings(Role role);

}
