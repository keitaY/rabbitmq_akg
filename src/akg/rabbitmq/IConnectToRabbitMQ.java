package akg.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 *  * Base class for objects that connect to a RabbitMQ Broker  
 */

public abstract class IConnectToRabbitMQ {
	protected Channel mModel = null;
	protected Connection mConnection;

	protected boolean Running;

	/**
	 *  *  * @param server The server address  * @param exchange The named
	 * exchange  * @param exchangeType The exchange type name  
	 */
	public IConnectToRabbitMQ() {
		
	}

	public void Dispose() {
		Running = false;

		try {
			if (mConnection != null)
				mConnection.close();
			if (mModel != null)
				mModel.abort();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 *  * Connect to the broker and create the exchange  * @return success  
	 */
	public boolean connectToRabbitMQ() {
		if (mModel != null && mModel.isOpen())
			return true;
		try {
			ConnectionFactory connectionFactory = new ConnectionFactory();
			connectionFactory.setHost(Constants.SERVER_DOMAIN);
			connectionFactory.setUsername(Constants.SERVER_ID);
			connectionFactory.setPassword(Constants.SERVER_PASSWORD);
			mConnection = connectionFactory.newConnection();
			mModel = mConnection.createChannel();
			mModel.exchangeDeclare(Constants.EXCHANGE, Constants.EX_TYPE, true);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}