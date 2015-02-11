package org.mqnaas.network.impl.reservation;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.i2cat.dana.mqnaas.capability.reservation.impl.ReservationCapability;
import net.i2cat.dana.mqnaas.capability.reservation.model.Device;
import net.i2cat.dana.mqnaas.capability.reservation.model.Reservation;
import net.i2cat.dana.mqnaas.capability.reservation.model.ReservationState;
import net.i2cat.dana.mqnaas.capability.reservation.utils.ModelUtils;

import org.mqnaas.core.api.IResource;
import org.mqnaas.core.api.IRootResource;
import org.mqnaas.core.api.IService;
import org.mqnaas.core.api.IServiceMetaData;
import org.mqnaas.core.api.Specification.Type;
import org.mqnaas.network.api.request.Period;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public abstract class ReservationManagementTest {

	protected static final String	DEVICE_ID_1	= "device01";
	protected static final String	DEVICE_ID_2	= "device02";
	protected static final String	DEVICE_ID_3	= "device03";
	protected static final String	SCOPE_NITOS	= "nitos";

	protected ReservationCapability	reservationCapability;

	class MockService implements IService {

		@Override
		public IResource getResource() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IServiceMetaData getMetadata() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getId() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	protected Reservation generateReservation(Date startDate, Date endDate, ReservationState reservationState) {

		Period period = new Period(startDate, endDate);

		Set<Device> devices = generateSampleDeviceSet();

		return ModelUtils.generateReservation(devices, new HashSet<IRootResource>(), period, reservationState);
	}

	protected Set<Device> generateSampleDeviceSet() {

		Device device1 = ModelUtils.generateDevice(DEVICE_ID_1, Type.OF_SWITCH, SCOPE_NITOS);
		Device device2 = ModelUtils.generateDevice(DEVICE_ID_2, Type.OF_SWITCH, SCOPE_NITOS);
		Device device3 = ModelUtils.generateDevice(DEVICE_ID_3, Type.OF_SWITCH, "");

		Set<Device> devices = new HashSet<Device>();
		devices.add(device1);
		devices.add(device2);
		devices.add(device3);

		return devices;
	}
}
