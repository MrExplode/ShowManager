package me.mrexplode.timecode.util;

import ch.bildspur.artnet.ArtNetServer;
import ch.bildspur.artnet.PortDescriptor;
import ch.bildspur.artnet.packets.ArtPollReplyPacket;
import lombok.experimental.UtilityClass;

import java.net.InetAddress;

@UtilityClass
public class Utils {

    public void setReplyPacket(ArtNetServer server, InetAddress ip) {
        ArtPollReplyPacket replyPacket = new ArtPollReplyPacket();
        replyPacket.setIp(ip);
        replyPacket.setShortName("TimecodeGen Node");
        replyPacket.setLongName("Timecode Generator Node by MrExplode");
        replyPacket.setVersionInfo(1);
        replyPacket.setSubSwitch(1);
        replyPacket.setOemCode(5);
        PortDescriptor port = new PortDescriptor();
        port.setCanInput(true);
        port.setCanOutput(true);
        replyPacket.setPorts(new PortDescriptor[] {port});

        replyPacket.translateData();
        server.setDefaultReplyPacket(replyPacket);
    }
}
