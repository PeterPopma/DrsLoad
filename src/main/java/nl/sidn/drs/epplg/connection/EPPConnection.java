package nl.sidn.drs.epplg.connection;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
public class EPPConnection {

	private SocketUtil socketUtil = null;

	public String readWrite(String msg){
		try {
			socketUtil.write(msg);
			return socketUtil.read();
		} catch (IOException e) {
			log.debug("error reading/writing message", e);
		}
		return "error";
	}

	public boolean send(String msg){
		try {
			socketUtil.write(msg);
		} catch (IOException e) {
			log.debug("error sending message", e);
			return false;
		}

		return true;
	}


	public String connect(String server, int port) {

		socketUtil = new SocketUtil();
		try {
			socketUtil.createSocket(server, port, 60000); //60 sec timeout
			return socketUtil.read();

		} catch (IOException e) {
			log.debug("error connecting", e);
		}

		return "error";
	}



	public void disConnect() {
		try {
			socketUtil.closeSocket();
		} catch (IOException e) {
			log.debug("error disconnecting", e);
		}

	}

}
