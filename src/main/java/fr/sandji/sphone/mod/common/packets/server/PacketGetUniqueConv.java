package fr.sandji.sphone.mod.common.packets.server;


import fr.aym.acslib.utils.packetserializer.SerializablePacket;
import fr.sandji.sphone.SPhone;
import fr.sandji.sphone.mod.common.items.ItemPhone;
import fr.sandji.sphone.mod.common.packets.client.PacketOpenConvContact;
import fr.sandji.sphone.mod.common.phone.Contact;
import fr.sandji.sphone.mod.server.bdd.MethodesBDDImpl;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGetUniqueConv extends SerializablePacket implements IMessage {
    public PacketGetUniqueConv() {
    }

    public PacketGetUniqueConv(Contact contact) {
        super(contact);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
    }

    public static class ServerHandler implements IMessageHandler<PacketGetUniqueConv, IMessage> {

        @Override
        public IMessage onMessage(PacketGetUniqueConv message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            int sim = ItemPhone.getSimCard(player.getHeldItemMainhand());
            if (sim == 0) {
                return null;
            }

            Contact contact = (Contact) message.getObjectsIn()[0];

            SPhone.network.sendTo(new PacketOpenConvContact(MethodesBDDImpl.getConversations(sim), MethodesBDDImpl.getConversation(sim, contact)), player);
            
            return null;
        }

    }
}
