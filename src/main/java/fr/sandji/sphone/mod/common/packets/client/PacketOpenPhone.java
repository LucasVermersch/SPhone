
/*
 * SPhone - Tous droits réservés. (by 0hSandji)
 */

package fr.sandji.sphone.mod.common.packets.client;

import fr.sandji.sphone.mod.client.gui.phone.GuiHome;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketOpenPhone implements IMessage {

    private String action;
    private String content;

    public PacketOpenPhone() {}

    public PacketOpenPhone(String action, String content) {
        this.action = action;
        this.content = content;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.action = ByteBufUtils.readUTF8String(buf);
        this.content = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.action);
        ByteBufUtils.writeUTF8String(buf, this.content);
    }

    public static class Handler implements IMessageHandler<PacketOpenPhone, IMessage> {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenPhone message, MessageContext ctx) {
            if (message.action.equals("home")) {
                IThreadListener thread = FMLCommonHandler.instance().getWorldThread(ctx.netHandler);
                thread.addScheduledTask(new Runnable()
                {
                    public void run()
                    {
                        Minecraft.getMinecraft().displayGuiScreen(new GuiHome().getGuiScreen());
                    }
                });
            }
            /*
            if (message.action.equals("openContacts")) {
                List<Contact> contactList = new ArrayList<Contact>();
                Gson gson = new Gson();
                Contact[] contacts = gson.fromJson(message.content, Contact[].class);
                for (Contact contact : contacts) {
                    contactList.add(new Contact(contact.getName(), contact.getLastname(), contact.getNumero(), contact.getNotes()));
                }
                Minecraft.getMinecraft().displayGuiScreen(new GuiContactsList(new GuiHome().getGuiScreen(), contactList).getGuiScreen());
            }*/
            return null;
        }
    }

}

