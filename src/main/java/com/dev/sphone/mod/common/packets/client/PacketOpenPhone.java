package com.dev.sphone.mod.common.packets.client;

import com.dev.sphone.mod.client.gui.phone.GuiBase;
import com.dev.sphone.mod.client.gui.phone.GuiHome;
import com.dev.sphone.mod.client.gui.phone.GuiNoSIM;
import com.dev.sphone.mod.client.gui.phone.apps.call.GuiCall;
import com.dev.sphone.mod.client.gui.phone.apps.call.GuiCallRequest;
import com.dev.sphone.mod.client.gui.phone.apps.call.GuiWaitCall;
import com.dev.sphone.mod.common.phone.Contact;
import fr.aym.acslib.utils.packetserializer.SerializablePacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenPhone extends SerializablePacket implements IMessage {

    private String action;
    private String content;
    private String contactTargetName;
    private String receiver;

    public PacketOpenPhone() {}

    public PacketOpenPhone(EnumAction action) {
        super(new Object[0]);
        this.action = action.name();
        this.content = "";
        this.contactTargetName = "";
        this.receiver = "";
    }

    public PacketOpenPhone(EnumAction action, String content) {
        super(new Object[0]);
        this.action = action.name();
        this.content = content;
        this.contactTargetName = "";
        this.receiver = "";
    }

    public PacketOpenPhone(EnumAction action, String content, String receiver) {
        super(new Object[0]);
        this.action = action.name();
        this.content = content;
        this.contactTargetName = "";
        this.receiver = receiver;
    }

    public PacketOpenPhone(EnumAction action, String content, String contactTargetName, Contact contact) {
        super(contact);
        this.action = action.name();
        this.content = content;
        this.contactTargetName = contactTargetName;
        this.receiver = "";
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        this.action = ByteBufUtils.readUTF8String(buf);
        this.content = ByteBufUtils.readUTF8String(buf);
        this.contactTargetName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        ByteBufUtils.writeUTF8String(buf, this.action);
        ByteBufUtils.writeUTF8String(buf, this.content);
        ByteBufUtils.writeUTF8String(buf, this.contactTargetName);
        ByteBufUtils.writeUTF8String(buf, this.receiver);
    }

    public static class Handler implements IMessageHandler<PacketOpenPhone, IMessage> {
        @Override
        public IMessage onMessage(PacketOpenPhone message, MessageContext ctx) {

            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    EnumAction action = EnumAction.valueOf(message.action);
                    switch (action) {
                        case HOME:
                            Minecraft.getMinecraft().displayGuiScreen(new GuiHome().getGuiScreen());
                            break;
                        case NOSIM:
                            Minecraft.getMinecraft().displayGuiScreen(new GuiNoSIM(message.content).getGuiScreen());
                            break;
                        case DONT_EXISTS:
                        case SEND_CALL:
                            Minecraft.getMinecraft().displayGuiScreen(new GuiCall(new GuiBase().getGuiScreen(), message.content).getGuiScreen());
                            break;

                        case WAIT_CALL:
                            EntityPlayer receiver = Minecraft.getMinecraft().world.getPlayerEntityByName(message.receiver);
                            Minecraft.getMinecraft().displayGuiScreen(new GuiWaitCall(new GuiBase().getGuiScreen(), message.content, receiver).getGuiScreen());
                            break;
                        case RECEIVE_CALL:
                            EntityPlayer receiver1 = Minecraft.getMinecraft().world.getPlayerEntityByName(message.receiver);
                            Minecraft.getMinecraft().displayGuiScreen(new GuiCallRequest(message.content, message.contactTargetName, (Contact) message.getObjectsIn()[0], receiver1).getGuiScreen());
                            break;
                    }
                }
            });
            return null;
        }
    }

    public enum EnumAction {
        HOME,
        NOSIM,
        DONT_EXISTS,
        RECEIVE_CALL,
        WAIT_CALL,
        SEND_CALL;

    }
}

