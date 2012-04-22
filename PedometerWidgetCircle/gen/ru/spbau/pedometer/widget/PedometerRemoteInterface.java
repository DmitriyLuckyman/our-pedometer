/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/maria/Dropbox/2semestr/PedometerWidget/src/ru/spbau/pedometer/widget/PedometerRemoteInterface.aidl
 */
package ru.spbau.pedometer.widget;
public interface PedometerRemoteInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements ru.spbau.pedometer.widget.PedometerRemoteInterface
{
private static final java.lang.String DESCRIPTOR = "ru.spbau.pedometer.widget.PedometerRemoteInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an ru.spbau.pedometer.widget.PedometerRemoteInterface interface,
 * generating a proxy if needed.
 */
public static ru.spbau.pedometer.widget.PedometerRemoteInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof ru.spbau.pedometer.widget.PedometerRemoteInterface))) {
return ((ru.spbau.pedometer.widget.PedometerRemoteInterface)iin);
}
return new ru.spbau.pedometer.widget.PedometerRemoteInterface.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getSteps:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getSteps();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements ru.spbau.pedometer.widget.PedometerRemoteInterface
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public int getSteps() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSteps, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getSteps = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public int getSteps() throws android.os.RemoteException;
}
