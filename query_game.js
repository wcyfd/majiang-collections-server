importPackage(com.randioo.majiang_collections_server.module.gm.component);
importPackage(com.randioo.majiang_collections_server.cache.local);
importPackage(com.randioo.randioo_server_base.utils);
importPackage(com.randioo.randioo_server_base.cache);

function query(lockString) {
	var gameQuery = SpringContext.getBean("gameQuery");
	var list = gameQuery.queryGame(lockString);
	return list;
}

query(lockString);
