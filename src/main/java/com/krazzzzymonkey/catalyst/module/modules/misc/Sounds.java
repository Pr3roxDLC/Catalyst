package com.krazzzzymonkey.catalyst.module.modules.misc;

import com.krazzzzymonkey.catalyst.Main;
import com.krazzzzymonkey.catalyst.events.PacketEvent;
import com.krazzzzymonkey.catalyst.managers.SoundTypes;
import com.krazzzzymonkey.catalyst.module.ModuleCategory;
import com.krazzzzymonkey.catalyst.module.Modules;
import com.krazzzzymonkey.catalyst.utils.SoundThread;
import com.krazzzzymonkey.catalyst.value.Mode;
import com.krazzzzymonkey.catalyst.value.types.ModeValue;
import dev.tigr.simpleevents.listener.EventHandler;
import dev.tigr.simpleevents.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Sounds extends Modules {

    public static ModeValue killSoundMode;
    public static ModeValue hitSoundMode;
    public static ModeValue popSoundMode;
    public static ModeValue explosionSoundMode;
    public static Random random = new Random();

    public static final File KILL_SOUND_DIR = new File(String.format("%s%s%s%s%s%s%s%s", Minecraft.getMinecraft().gameDir, File.separator, Main.NAME, File.separator, "Sounds", File.separator, "KillSounds", File.separator));
    public static final File POP_SOUND_DIR = new File(String.format("%s%s%s%s%s%s%s%s", Minecraft.getMinecraft().gameDir, File.separator, Main.NAME, File.separator, "Sounds", File.separator, "PopSounds", File.separator));
    public static final File HIT_SOUND_DIR = new File(String.format("%s%s%s%s%s%s%s%s", Minecraft.getMinecraft().gameDir, File.separator, Main.NAME, File.separator, "Sounds", File.separator, "HitSounds", File.separator));
    public static final File EXPLOSION_SOUND_DIR = new File(String.format("%s%s%s%s%s%s%s%s", Minecraft.getMinecraft().gameDir, File.separator, Main.NAME, File.separator, "Sounds", File.separator, "ExplosionSounds", File.separator));


    public Sounds() {
        super("Sounds", ModuleCategory.MISC, "Plays Custom sounds for certain events");

        if(!KILL_SOUND_DIR.exists())KILL_SOUND_DIR.mkdirs();
        if(!POP_SOUND_DIR.exists())POP_SOUND_DIR.mkdirs();
        if(!HIT_SOUND_DIR.exists())HIT_SOUND_DIR.mkdirs();
        if(!EXPLOSION_SOUND_DIR.exists())EXPLOSION_SOUND_DIR.mkdirs();


        ArrayList<Mode> killSoundsList = new ArrayList<>();
        Arrays.stream(Objects.requireNonNull(KILL_SOUND_DIR.listFiles())).forEach(n -> killSoundsList.add(new Mode(n.getName(), false)));
        killSoundsList.add(new Mode("Random", false));
        killSoundsList.add(new Mode("None", false));
        killSoundMode = new ModeValue("KillSounds", killSoundsList.toArray(new Mode[0]));

        ArrayList<Mode> hitSoundsList = new ArrayList<>();
        Arrays.stream(Objects.requireNonNull(HIT_SOUND_DIR.listFiles())).forEach(n -> hitSoundsList.add(new Mode(n.getName(), false)));
        hitSoundsList.add(new Mode("Random", false));
        hitSoundsList.add(new Mode("None", false));
        hitSoundMode = new ModeValue("HitSounds", hitSoundsList.toArray(new Mode[0]));

        ArrayList<Mode> popSoundsList = new ArrayList<>();
        Arrays.stream(Objects.requireNonNull(POP_SOUND_DIR.listFiles())).forEach(n -> popSoundsList.add(new Mode(n.getName(), false)));
        popSoundsList.add(new Mode("Random", false));
        popSoundsList.add(new Mode("None", false));
        popSoundMode = new ModeValue("PopSounds", popSoundsList.toArray(new Mode[0]));

        ArrayList<Mode> explosionSoundsList = new ArrayList<>();
        Arrays.stream(Objects.requireNonNull(EXPLOSION_SOUND_DIR.listFiles())).forEach(n -> explosionSoundsList.add(new Mode(n.getName(), false)));
        explosionSoundsList.add(new Mode("Random", false));
        explosionSoundsList.add(new Mode("None", false));
        explosionSoundMode = new ModeValue("ExplosionSounds", explosionSoundsList.toArray(new Mode[0]));

        addValue(killSoundMode, hitSoundMode, popSoundMode, explosionSoundMode);

    }

    public static void onExplosion(){
        if(!SoundThread.isOpen) {
            //TODO make it so it only plays the sound if the player damaged someone, not on every damage event
            if (!explosionSoundMode.getMode("None").isToggled()) {
                if (explosionSoundMode.getMode("Random").isToggled()) {
                    try {
                        if(explosionSoundMode.getModes().length - 2 == 0)return;
                        //TODO Find out why this is throwing negative ArrayIndexOutOfBounce exceptions and remove Math.abs
                        playSound(SoundTypes.EXPLOSION, explosionSoundMode.getModes()[Math.abs(random.nextInt() % (explosionSoundMode.getModes().length - 2))].getName());
                    } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Arrays.stream(explosionSoundMode.getModes()).filter(Mode::isToggled).findFirst().ifPresent(n -> {
                        try {
                            playSound(SoundTypes.EXPLOSION, n.getName());
                        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }


    public static void playSound(SoundTypes soundType, String name) throws IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
        File play = null;

        switch (soundType) {
            case HIT:
                play = Arrays.stream(HIT_SOUND_DIR.listFiles()).filter(n -> n.getName().equalsIgnoreCase(name)).collect(Collectors.toList()).get(0);
                break;
            case POP:
                play = Arrays.stream(POP_SOUND_DIR.listFiles()).filter(n -> n.getName().equalsIgnoreCase(name)).collect(Collectors.toList()).get(0);
                break;
            case KILL:
                play = Arrays.stream(KILL_SOUND_DIR.listFiles()).filter(n -> n.getName().equalsIgnoreCase(name)).collect(Collectors.toList()).get(0);
                break;
            case EXPLOSION:
                play = Arrays.stream(EXPLOSION_SOUND_DIR.listFiles()).filter(n -> n.getName().equalsIgnoreCase(name)).collect(Collectors.toList()).get(0);
                break;
        }

        Thread f = new Thread(new SoundThread(play));
        f.start();
    }
    HashSet<EntityPlayer> players = new HashSet<>();
    @EventHandler
    private final EventListener<PacketEvent> onPacketReceive = new EventListener<>(event -> {
        if(event.getSide() == PacketEvent.Side.IN) {

            if (event.getPacket() instanceof SPacketEntityStatus) {
                SPacketEntityStatus sPacketEntityStatus = (SPacketEntityStatus) event.getPacket();
                switch (sPacketEntityStatus.getOpCode()) {
                    case 2:
                        if (players.contains(sPacketEntityStatus.getEntity(mc.world))) {
                            if (!hitSoundMode.getMode("None").isToggled()) {
                                if (hitSoundMode.getMode("Random").isToggled()) {
                                    try {
                                        if(hitSoundMode.getModes().length - 2 == 0)return;
                                        //TODO Find out why this is throwing negative ArrayIndexOutOfBounce exceptions and remove Math.abs
                                        playSound(SoundTypes.HIT, hitSoundMode.getModes()[Math.abs(random.nextInt() % (hitSoundMode.getModes().length - 2))].getName());
                                    } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Arrays.stream(hitSoundMode.getModes()).filter(Mode::isToggled).findFirst().ifPresent(n -> {
                                        try {
                                            playSound(SoundTypes.HIT, n.getName());
                                        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }
                            }
                        }
                        break;
                    case 3:
                        if (players.contains(sPacketEntityStatus.getEntity(mc.world))) {
                            if (!killSoundMode.getMode("None").isToggled()) {
                                if (killSoundMode.getMode("Random").isToggled()) {
                                    try {
                                        if(killSoundMode.getModes().length - 2 == 0)return;
                                        //TODO Find out why this is throwing negative ArrayIndexOutOfBounce exceptions and remove Math.abs
                                        playSound(SoundTypes.KILL, killSoundMode.getModes()[Math.abs(random.nextInt() % (killSoundMode.getModes().length - 2))].getName());
                                    } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Arrays.stream(killSoundMode.getModes()).filter(Mode::isToggled).findFirst().ifPresent(n -> {
                                        try {
                                            playSound(SoundTypes.KILL, n.getName());
                                        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }
                            }
                        }
                        break;
                    case 35:
                        if (!popSoundMode.getMode("None").isToggled()) {
                            if (popSoundMode.getMode("Random").isToggled()) {
                                try {
                                    if(popSoundMode.getModes().length - 2 == 0)return;
                                    //TODO Find out why this is throwing negative ArrayIndexOutOfBounce exceptions and remove Math.abs
                                    playSound(SoundTypes.POP, popSoundMode.getModes()[Math.abs(random.nextInt() % (popSoundMode.getModes().length - 2))].getName());
                                } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Arrays.stream(popSoundMode.getModes()).filter(Mode::isToggled).findFirst().ifPresent(n -> {
                                    try {
                                        playSound(SoundTypes.POP, n.getName());
                                    } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }
                        break;

                }
            }

        }
    });




    @EventHandler
    private final EventListener<PacketEvent> onPacketSend = new EventListener<>(event -> {
        if(event.getSide() == PacketEvent.Side.OUT) {

            if (event.getPacket() instanceof CPacketUseEntity) {
                CPacketUseEntity cPacketUseEntity = (CPacketUseEntity) event.getPacket();
                if (cPacketUseEntity.getAction() == CPacketUseEntity.Action.ATTACK) {
                    if (cPacketUseEntity.getEntityFromWorld(mc.world) instanceof EntityPlayer) {
                        if (!players.contains(cPacketUseEntity.getEntityFromWorld(mc.world))) {
                            players.add((EntityPlayer) cPacketUseEntity.getEntityFromWorld(mc.world));
                        }
                    }
                }
            }
        }
    });


}

