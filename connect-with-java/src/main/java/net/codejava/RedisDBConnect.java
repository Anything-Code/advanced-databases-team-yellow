package net.codejava;

import redis.clients.jedis.Jedis;

public class RedisDBConnect implements AutoCloseable{
	public RedisDBConnect() throws Exception {
		try {
			Jedis jedis = new Jedis("redis://localhost:6379");
			System.out.println("connected!");
			System.out.println("Server ping" + jedis.ping());
		}catch(Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
