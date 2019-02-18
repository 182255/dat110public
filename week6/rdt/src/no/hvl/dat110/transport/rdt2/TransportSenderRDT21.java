package no.hvl.dat110.transport.rdt2;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import no.hvl.dat110.transport.*;

public class TransportSenderRDT21 extends TransportSender implements ITransportProtocolEntity {

	public enum RDT2SenderStates {
		WAITDATA, WAITACKNAK;
	}

	private LinkedBlockingQueue<byte[]> outdataqueue;
	private LinkedBlockingQueue<SegmentRDT2> recvqueue;
	private RDT2SenderStates state;

	public TransportSenderRDT21() {
		super("TransportSender");
		recvqueue = new LinkedBlockingQueue<SegmentRDT2>();
		outdataqueue = new LinkedBlockingQueue<byte[]>();
		state = RDT2SenderStates.WAITDATA;
	}

	public void rdt_send(byte[] data) {

		try {
			
			outdataqueue.put(data);
			
		} catch (InterruptedException ex) {
			System.out.println("TransportSender thread " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void rdt_recv(Segment segment) {

		System.out.println("[Transport:Receiver ] rdt_recv: " + segment.toString());

		try {
			
			recvqueue.put((SegmentRDT2) segment);
			
		} catch (InterruptedException ex) {
			System.out.println("TransportSenderRDT2 thread " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private byte[] data = null;

	public void doProcess() {

		switch (state) {

		case WAITDATA:

			doWaitData();

			break;

		case WAITACKNAK:

			doWaitAckNak();

			break;

		default:
			break;
		}

	}

	private void doWaitData() {
		
		try {
			
			data = outdataqueue.poll(2, TimeUnit.SECONDS);

			if (data != null) { // something to send

				udt_send(new SegmentRDT2(data));

				state = RDT2SenderStates.WAITACKNAK;
			}

		} catch (InterruptedException ex) {
			System.out.println("TransportSenderRDT2 thread " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void doWaitAckNak() {
		
		try {

			SegmentRDT2 acksegment = recvqueue.poll(2, TimeUnit.SECONDS);

			if (acksegment != null) {

				SegmentType type = acksegment.getType();

				if (type == SegmentType.ACK) {

					System.out.println("[Transport:Sender   ] ACK ");
					data = null;
					state = RDT2SenderStates.WAITDATA;
					
				} else {
					System.out.println("[Transport:Sender   ] NAK ");
					udt_send(new SegmentRDT2(data));
				}
			}
			
		} catch (InterruptedException ex) {
			System.out.println("TransportSenderRDT2 thread " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
