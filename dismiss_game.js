importPackage(com.randioo.majiang_collections_server.module.gm.component);
importPackage(com.randioo.majiang_collections_server.cache.local);
importPackage(com.randioo.randioo_server_base.utils);
importPackage(com.randioo.randioo_server_base.cache);

//function dismiss(lockString) {
//	var playbackManager = SpringContext.getBean("playbackManager");
//	var gameId = GameCache.getGameLockStringMap().get(lockString + "");
//	println(lockString);
//	println(gameId);
//	if (gameId == null) {
//		return;
//	}
//	var game = GameCache.getGameMap().get(gameId);
//	// 移除录像
//	playbackManager.remove(game);
//
//	println(accounts);
//	for (var i = 0; i < accounts.length; i++) {
//		var account = accounts[i];
//		println(account);
//		var role = RoleCache.getRoleByAccount(account);
//		println(role);
//		if (role != null) {
//			role.setGameId(0);
//			role.setGameOverSC(null);
//			role.setGameConfigData(null);
//		}
//	}
//
////	var roleGameInfoCollections = game.getRoleIdMap().values();
////	println(roleGameInfoCollections);
////	for (var i = 0; i < roleGameInfoCollections.length; i++) {
////		var roleId = roleGameInfoCollections[i].roleId;
////		println(roleId);
////		var role = RoleCache.getRoleById(roleId);
////		println(role);
////		if (role != null) {
////			role.setGameId(0);
////			role.setGameOverSC(null);
////			role.setGameConfigData(null);
////		}
////	}
//
//	var key = game.getLockKey();
//	if (key != null) {
//		GameCache.getGameLockStringMap().remove(lockString);
//		key.recycle();
//	}
//
//	// 将游戏从缓存池中移除
//	GameCache.getGameMap().remove(game.getGameId());
//}

 var dismissGame = SpringContext.getBean("dismissGame");
 dismissGame.dismissGameByLockString(lockString);

//dismiss(lockString);
