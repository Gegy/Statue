package net.gegy1000.statue;

import net.gegy1000.statue.server.ServerProxy;
import net.gegy1000.statue.server.message.CreateTextureMessage;
import net.gegy1000.statue.server.message.RemoveTextureMessage;
import net.gegy1000.statue.server.message.SetLockedMessage;
import net.gegy1000.statue.server.message.SetModelMessage;
import net.gegy1000.statue.server.message.SetPropertiesMessage;
import net.gegy1000.statue.server.message.TextureSectionMessage;
import net.ilexiconn.llibrary.server.network.NetworkWrapper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Mod(modid = Statue.MODID, name = "Statue", version = Statue.VERSION, dependencies = "required-after:llibrary@[" + Statue.LLIBRARY_VERSION + ",)")
public class Statue {
    public static final String MODID = "statue";
    public static final String VERSION = "1.0.0";
    public static final String LLIBRARY_VERSION = "1.7.1";

    @Mod.Instance(Statue.MODID)
    public static Statue INSTANCE;

    @SidedProxy(serverSide = "net.gegy1000.statue.server.ServerProxy", clientSide = "net.gegy1000.statue.client.ClientProxy")
    public static ServerProxy PROXY;

    @NetworkWrapper({ SetModelMessage.class, CreateTextureMessage.class, TextureSectionMessage.class, RemoveTextureMessage.class, SetPropertiesMessage.class, SetLockedMessage.class })
    public static SimpleNetworkWrapper WRAPPER;

    public static DecimalFormat DEFAULT_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ENGLISH);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        DEFAULT_FORMAT = new DecimalFormat("#.#", symbols);
        DEFAULT_FORMAT.setMaximumFractionDigits(1);
    }

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        PROXY.onPreInit();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        PROXY.onInit();
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        PROXY.onPostInit();
    }
}
