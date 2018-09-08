package ru.vonabe.packet;

public interface Packet {

    public void handler();

    public void setData(FastData data);

    public FastData getData();

}
