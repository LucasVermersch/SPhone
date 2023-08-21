package com.dev.sphone.mod.common.items;

import com.dev.sphone.SPhone;
import com.dev.sphone.mod.client.tempdata.PhoneSettings;
import com.dev.sphone.mod.common.packets.client.PacketOpenPhone;
import com.dev.sphone.mod.common.register.ItemsRegister;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Objects;

public class ItemPhone extends Item {

    public static final String SIM_KEY_TAG = "simcard";
    private static ItemStackHandler handler;

    public ItemPhone(String name, CreativeTabs creativeTabs, int stackcount) {
        this.setTranslationKey(name);
        this.setRegistryName(name);
        this.setCreativeTab(creativeTabs);
        this.setMaxStackSize(stackcount);
        ItemsRegister.INSTANCE.getItems().add(this);
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!world.isRemote && hand == EnumHand.MAIN_HAND) {
            if(player.isSneaking()) {
                player.openGui(SPhone.INSTANCE, 7, world, 0, 0, 0);
                return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
            }
            ItemStack stack = player.getHeldItem(hand);

            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("simcard")) {
                SPhone.network.sendTo(new PacketOpenPhone("home", ""), (EntityPlayerMP) player);
            } else {
                SPhone.network.sendTo(new PacketOpenPhone("nosim", ""), (EntityPlayerMP) player);
            }

            if (stack.getTagCompound().hasKey("inventory")) {
                handler = new ItemStackHandler(1);
                handler.deserializeNBT(stack.getTagCompound().getCompoundTag("inventory"));

                if(handler.getStackInSlot(0).getItem() == Items.AIR) {
                    SPhone.network.sendTo(new PacketOpenPhone("nosim", ""), (EntityPlayerMP) player);
                } else if (handler.getStackInSlot(0).getTagCompound().getInteger(ItemSim.SIM_KEY_TAG) == 0) {
                    SPhone.network.sendTo(new PacketOpenPhone("nosim", "unregistred"), (EntityPlayerMP) player);
                } else {
                    SPhone.network.sendTo(new PacketOpenPhone("home", ""), (EntityPlayerMP) player);
                    if(getTagCompound(stack).hasKey("settings")) {
                        getTagCompound(stack).setTag("settings", new PhoneSettings("acsgui").serializeNBT());
                    }
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            tooltip.add("");
            tooltip.add("-> Version du Téléphone : " + SPhone.VERSION);
            tooltip.add("-> Mode Développeur : " + SPhone.DEV_MOD);
            tooltip.add("");
        } else {
            if (getSimCard(stack) == 0) {
                tooltip.add("-> Aucune Carte Sim Injecté");
            } else if (getSimCard(stack) == -1) {
                tooltip.add("-> Carte Sim : Non Enregistré");
            } else {
                tooltip.add("-> Carte Sim : " + getSimCard(stack));
            }
            tooltip.add("-> Batterie : 100%");
            tooltip.add("");
            tooltip.add("Appuyer sur SHIFT pour plus d'informations.");
        }
        super.addInformation(stack, world, tooltip, flagIn);
    }

    public static boolean isPhone(ItemStack stack) {
        return stack.getItem() == ItemsRegister.ITEM_PHONE;
    }

    public static NBTTagCompound getTagCompound(ItemStack stack) {
        NBTTagCompound nbt;
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        nbt = stack.getTagCompound();
        return nbt;
    }

    public static void setSimCard(EntityPlayer player, ItemStack stack, int sim) {
        if (!isPhone(stack)) return;
        NBTTagCompound nbt = getTagCompound(stack);
        nbt.setInteger(SIM_KEY_TAG, sim);
        sendActionChat(player, "Vous avez injecté la carte sim : " + sim, false);
    }

    public static int getSimCard(ItemStack stack) {
        if (isPhone(stack)) {
            if(!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

            handler = new ItemStackHandler(1);
            handler.deserializeNBT(stack.getTagCompound().getCompoundTag("inventory"));

            if(handler.getStackInSlot(0).getItem() == Items.AIR) return 0;
            if(Objects.requireNonNull(handler.getStackInSlot(0).getTagCompound()).getInteger(ItemSim.SIM_KEY_TAG) == 0) return -1;

            return Objects.requireNonNull(handler.getStackInSlot(0).getTagCompound()).getInteger(ItemSim.SIM_KEY_TAG);
        }
        return 0;
    }

    //return item sim
    public static ItemSim getSimCardItem(ItemStack stack) {
        if (isPhone(stack)) {
            if(!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

            handler = new ItemStackHandler(1);
            handler.deserializeNBT(stack.getTagCompound().getCompoundTag("inventory"));

            if(handler.getStackInSlot(0).getItem() == Items.AIR) return null;
            if(Objects.requireNonNull(handler.getStackInSlot(0).getTagCompound()).getInteger(ItemSim.SIM_KEY_TAG) == 0) return null;

            return (ItemSim) handler.getStackInSlot(0).getItem();
        }
        return null;
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static void sendActionChat(EntityPlayer player, String msg, Boolean actionbar) {
        player.sendStatusMessage(new TextComponentString(TextFormatting.GREEN + msg), actionbar);
    }

    public static void sendErrorChat(EntityPlayer player, String msg, Boolean actionbar) {
        player.sendStatusMessage(new TextComponentString(TextFormatting.RED + msg), actionbar);
    }
}
