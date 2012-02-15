package com.dianping.cat.hadoop.hdfs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.dianping.cat.hadoop.hdfs.HdfsMessageStorage;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.MessageProducer;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.io.InMemoryQueue;
import com.dianping.cat.message.spi.MessageStorage;
import com.dianping.cat.message.spi.MessageTree;

@RunWith(JUnit4.class)
public class HdfsMessageStorageTest extends CatTestCase {
	@Test
	public void test() throws Exception {
		MessageStorage storage = lookup(MessageStorage.class, "hdfs");
		MessageProducer producer = lookup(MessageProducer.class);
		InMemoryQueue queue = lookup(InMemoryQueue.class);

		for (int i = 0; i < 100; i++) {
			Transaction t = producer.newTransaction("URL", "MyPage");

			try {
				// do your business here
				t.addData("k1", "v1");
				t.addData("k2", "v2");
				t.addData("k3", "v3");

				Thread.sleep(10);

				producer.logEvent("URL", "Payload", Message.SUCCESS, "host=my-host&ip=127.0.0.1&agent=...");
				producer.logEvent("URL", "Payload", Message.SUCCESS, "host=my-host&ip=127.0.0.1&agent=...");
				producer.logEvent("URL", "Payload", Message.SUCCESS, "host=my-host&ip=127.0.0.1&agent=...");
				producer.logEvent("URL", "Payload", Message.SUCCESS, "host=my-host&ip=127.0.0.1&agent=...");
				t.setStatus(Message.SUCCESS);
			} catch (Exception e) {
				t.setStatus(e);
			} finally {
				t.complete();
			}

			MessageTree tree = queue.poll(0);

			storage.store(tree);
		}

		((HdfsMessageStorage) storage).dispose();
	}
}
