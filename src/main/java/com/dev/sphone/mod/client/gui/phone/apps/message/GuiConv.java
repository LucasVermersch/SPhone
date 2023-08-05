package com.dev.sphone.mod.client.gui.phone.apps.message;

import com.dev.sphone.SPhone;
import com.dev.sphone.mod.client.gui.layout.CustomGridLayout;
import com.dev.sphone.mod.client.gui.phone.GuiBase;
import com.dev.sphone.mod.common.packets.server.PacketSendMessage;
import com.dev.sphone.mod.common.phone.Conversation;
import com.dev.sphone.mod.common.phone.Message;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.aym.acsguis.component.panel.GuiScrollPane;
import fr.aym.acsguis.component.textarea.GuiLabel;
import fr.aym.acsguis.component.textarea.GuiTextField;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class GuiConv extends GuiBase {

    private final Conversation conv;

    public GuiConv(GuiScreen parent, Conversation conv) {
        super(parent);
        this.conv = conv;
    }

    @Override
    public void GuiInit() {
        super.GuiInit();

        GuiLabel AppTitle = new GuiLabel(conv.getSender().getName());
        AppTitle.setCssId("app_title");
        getBackground().add(AppTitle);

        GuiScrollPane contacts_list = new GuiScrollPane() {
            public double max = 0;

            @Override
            public void updateSlidersVisibility() {
                super.updateSlidersVisibility();
                if (ySlider == null) return;
                if (max != ySlider.getMax()) {
                    max = ySlider.getMax();
                    ySlider.setValue(ySlider.getMax());
                }

            }
        };
        contacts_list.setCssClass("contacts_list");
        contacts_list.setLayout(new CustomGridLayout(-1, 70, 5, CustomGridLayout.GridDirection.HORIZONTAL, 1));

        //trier les conversations par date de dernier message
        //conv.sort((o1, o2) -> getDate(o2.getLastUpdate()).compareTo(getDate(o1.getLastUpdate())));

        for (Message c : conv.getMessages()) {

            GuiPanel messagePanel = new GuiPanel();

            GuiLabel labelMessage = new GuiLabel("");
            labelMessage.setMaxTextLength(240);
            labelMessage.setText(c.getMessage());
            labelMessage.setCssId("contact_message");
            messagePanel.add(labelMessage);

            if (!c.getSender().equals(conv.getSender().getNumero())) {
                messagePanel.setCssId("contact_background_me");
            } else {
                messagePanel.setCssId("contact_background");
            }

            GuiLabel date = new GuiLabel(getDate(c.getDate()));
            date.setCssId("date");
            //check si c'est aujourd'hui
            date.setHoveringText(Collections.singletonList(getHour(c.getDate()).toString()));
            messagePanel.add(date);

            contacts_list.add(messagePanel);

        }

        getBackground().add(contacts_list);

        GuiTextField message = new GuiTextField();
        message.setCssClass("message");
        message.setHintText("➜ votre message");
        getBackground().add(message);

        GuiPanel send = new GuiPanel();
        send.setCssClass("send");
        send.addClickListener((mouseX, mouseY, mouseButton) -> {
            SPhone.network.sendToServer(new PacketSendMessage(message.getText(), conv));
            //close gui
            Minecraft.getMinecraft().displayGuiScreen(null);
        });
        getBackground().add(send);
        contacts_list.getySlider().setValue(contacts_list.getySlider().getMax());
    }

    //get date et si c'est aujourd'hui on affiche l'heure
    public String getDate(long date) {
        Date d = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String today = sdf.format(new Date());
        if (sdf.format(d).equals(today)) {
            sdf = new SimpleDateFormat("HH:mm");
        }
        return sdf.format(d);
    }

    //get heure
    public String getHour(long date) {
        Date d = new Date(date);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(d);
    }

    public List<ResourceLocation> getCssStyles() {
        List<ResourceLocation> styles = new ArrayList<>();
        styles.add(super.getCssStyles().get(0));
        styles.add(new ResourceLocation("sphone:css/conv.css"));
        return styles;
    }
}
