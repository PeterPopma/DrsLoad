package nl.sidn.eppload.connection;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class SocketUtil {

	private Socket tcpSocket = new Socket();

	private final int BUF_SIZE = 4096;
	private DataInputStream in;
	private DataOutputStream out;


	public void createSocket(String host, int port, int soTimeout)
			throws IOException {

		tcpSocket.connect(new InetSocketAddress(host, port), soTimeout);
		tcpSocket.setSoTimeout(soTimeout);
		in = new DataInputStream(new BufferedInputStream(tcpSocket
				.getInputStream()));
		out = new DataOutputStream(new BufferedOutputStream(tcpSocket
				.getOutputStream(), BUF_SIZE));

	}

	public void createSocket(String host, int port) throws IOException {
		this.createSocket(host, port, 0);
	}

	public void createSocket(String host) throws IOException {
		this.createSocket(host, 700, 0);
	}

	public boolean isSocketConected() {
		return tcpSocket.isConnected();
	}

	public boolean isSocketBound() {
		return tcpSocket.isBound();
	}

	public boolean isSocketClosed() {
		return tcpSocket.isClosed();
	}

	public void closeSocket() throws IOException {
		tcpSocket.getOutputStream().flush();
		tcpSocket.getInputStream().close();
		tcpSocket.close();
	}

	public String read() throws IOException {
		// RFC 4934 #4. Data Unit Format
		//
		// The EPP data unit contains two fields: a 32-bit header that describes
		// the total length of the data unit, and the EPP XML instance. The
		// length of the EPP XML instance is determined by subtracting four
		// octets from the total length of the data unit. A receiver must
		// successfully read that many octets to retrieve the complete EPP XML
		// instance before processing the EPP message.
		//
		// EPP Data Unit Format (one tick mark represents one bit position):
		//
		// 0 1 2 3
		// 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
		// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		// | Total Length |
		// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		// | EPP XML Instance |
		// +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		//
		// Total Length (32 bits): The total length of the EPP data unit
		// measured in octets in network (big endian) byte order. The octets
		// contained in this field MUST be included in the total length
		// calculation.
		//
		// EPP XML Instance (variable length): The EPP XML instance carried in
		// the data unit.
		//
		String eppCommand = null;

		int size = in.readInt() - 4;
		if (size > 0) {
			// read the xml
			byte[] inputBuffer = new byte[size];
			in.readFully(inputBuffer, 0, size);
			eppCommand = new String(inputBuffer, "UTF-8");
		}

		log.debug("receive: " + eppCommand);

		return eppCommand;
	}

	public void write(String str) throws IOException {
		// EPP Protocol requires the first 32 bits to hold the number of octets
		// send.
		log.debug("send: " + str);
		out.writeInt(str.getBytes().length + 4);
		out.write(str.getBytes());
		out.flush();
	}
}
