/*package com.krazzzzymonkey.catalyst.command;

import com.krazzzzymonkey.catalyst.managers.FileManager;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import com.krazzzzymonkey.catalyst.utils.visual.ImageUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MapDump extends Command {

    private final Map<Integer, Color> colorPalette = new HashMap<>();

    public MapDump() {
        super("mapdump");
        colorPalette.put(0, new Color(0, 0, 0, 0));
        colorPalette.put(1, new Color(0, 0, 0, 0));
        colorPalette.put(2, new Color(0, 0, 0, 0));
        colorPalette.put(3, new Color(0, 0, 0, 0));
        colorPalette.put(4, new Color(89, 125, 39));
        colorPalette.put(5, new Color(109, 153, 48));
        colorPalette.put(6, new Color(127, 178, 56));
        colorPalette.put(7, new Color(67, 94, 29));
        colorPalette.put(8, new Color(174, 164, 115));
        colorPalette.put(9, new Color(213, 201, 140));
        colorPalette.put(10, new Color(247, 233, 163));
        colorPalette.put(11, new Color(130, 123, 86));
        colorPalette.put(12, new Color(140, 140, 140));
        colorPalette.put(13, new Color(171, 171, 171));
        colorPalette.put(14, new Color(199, 199, 199));
        colorPalette.put(15, new Color(105, 105, 105));
        colorPalette.put(16, new Color(180, 0, 0));
        colorPalette.put(17, new Color(220, 0, 0));
        colorPalette.put(18, new Color(255, 0, 0));
        colorPalette.put(19, new Color(135, 0, 0));
        colorPalette.put(20, new Color(112, 112, 180));
        colorPalette.put(21, new Color(138, 138, 220));
        colorPalette.put(22, new Color(160, 160, 255));
        colorPalette.put(23, new Color(84, 84, 135));
        colorPalette.put(24, new Color(117, 117, 117));
        colorPalette.put(25, new Color(144, 144, 144));
        colorPalette.put(26, new Color(167, 167, 167));
        colorPalette.put(27, new Color(88, 88, 88));
        colorPalette.put(28, new Color(0, 87, 0));
        colorPalette.put(29, new Color(0, 106, 0));
        colorPalette.put(30, new Color(0, 124, 0));
        colorPalette.put(31, new Color(0, 65, 0));
        colorPalette.put(32, new Color(180, 180, 180));
        colorPalette.put(33, new Color(220, 220, 220));
        colorPalette.put(34, new Color(255, 255, 255));
        colorPalette.put(35, new Color(135, 135, 135));
        colorPalette.put(36, new Color(115, 118, 129));
        colorPalette.put(37, new Color(141, 144, 158));
        colorPalette.put(38, new Color(164, 168, 184));
        colorPalette.put(39, new Color(86, 88, 97));
        colorPalette.put(40, new Color(106, 76, 54));
        colorPalette.put(41, new Color(130, 94, 66));
        colorPalette.put(42, new Color(151, 109, 77));
        colorPalette.put(43, new Color(79, 57, 40));
        colorPalette.put(44, new Color(79, 79, 79));
        colorPalette.put(45, new Color(96, 96, 96));
        colorPalette.put(46, new Color(112, 112, 112));
        colorPalette.put(47, new Color(59, 59, 59));
        colorPalette.put(48, new Color(45, 45, 180));
        colorPalette.put(49, new Color(55, 55, 220));
        colorPalette.put(50, new Color(64, 64, 255));
        colorPalette.put(51, new Color(33, 33, 135));
        colorPalette.put(52, new Color(100, 84, 50));
        colorPalette.put(53, new Color(123, 102, 62));
        colorPalette.put(54, new Color(143, 119, 72));
        colorPalette.put(55, new Color(75, 63, 38));
        colorPalette.put(56, new Color(180, 177, 172));
        colorPalette.put(57, new Color(220, 217, 211));
        colorPalette.put(58, new Color(255, 252, 245));
        colorPalette.put(59, new Color(135, 133, 129));
        colorPalette.put(60, new Color(152, 89, 36));
        colorPalette.put(61, new Color(186, 109, 44));
        colorPalette.put(62, new Color(216, 127, 51));
        colorPalette.put(63, new Color(114, 67, 27));
        colorPalette.put(64, new Color(125, 53, 152));
        colorPalette.put(65, new Color(153, 65, 186));
        colorPalette.put(66, new Color(178, 76, 216));
        colorPalette.put(67, new Color(94, 40, 114));
        colorPalette.put(68, new Color(72, 108, 152));
        colorPalette.put(69, new Color(88, 132, 186));
        colorPalette.put(70, new Color(102, 153, 216));
        colorPalette.put(71, new Color(54, 81, 114));
        colorPalette.put(72, new Color(161, 161, 36));
        colorPalette.put(73, new Color(197, 197, 44));
        colorPalette.put(74, new Color(229, 229, 51));
        colorPalette.put(75, new Color(121, 121, 27));
        colorPalette.put(76, new Color(89, 144, 17));
        colorPalette.put(77, new Color(109, 176, 21));
        colorPalette.put(78, new Color(127, 204, 25));
        colorPalette.put(79, new Color(67, 108, 13));
        colorPalette.put(80, new Color(170, 89, 116));
        colorPalette.put(81, new Color(208, 109, 142));
        colorPalette.put(82, new Color(242, 127, 165));
        colorPalette.put(83, new Color(128, 67, 87));
        colorPalette.put(84, new Color(53, 53, 53));
        colorPalette.put(85, new Color(65, 65, 65));
        colorPalette.put(86, new Color(76, 76, 76));
        colorPalette.put(87, new Color(40, 40, 40));
        colorPalette.put(88, new Color(108, 108, 108));
        colorPalette.put(89, new Color(132, 132, 132));
        colorPalette.put(90, new Color(153, 153, 153));
        colorPalette.put(91, new Color(81, 81, 81));
        colorPalette.put(92, new Color(53, 89, 108));
        colorPalette.put(93, new Color(65, 109, 132));
        colorPalette.put(94, new Color(76, 127, 153));
        colorPalette.put(95, new Color(40, 67, 81));
        colorPalette.put(96, new Color(89, 44, 125));
        colorPalette.put(97, new Color(109, 54, 153));
        colorPalette.put(98, new Color(127, 63, 178));
        colorPalette.put(99, new Color(67, 33, 94));
        colorPalette.put(100, new Color(36, 53, 125));
        colorPalette.put(101, new Color(44, 65, 153));
        colorPalette.put(102, new Color(51, 76, 178));
        colorPalette.put(103, new Color(27, 40, 94));
        colorPalette.put(104, new Color(72, 53, 36));
        colorPalette.put(105, new Color(88, 65, 44));
        colorPalette.put(106, new Color(102, 76, 51));
        colorPalette.put(107, new Color(54, 40, 27));
        colorPalette.put(108, new Color(72, 89, 36));
        colorPalette.put(109, new Color(88, 109, 44));
        colorPalette.put(110, new Color(102, 127, 51));
        colorPalette.put(111, new Color(54, 67, 27));
        colorPalette.put(112, new Color(108, 36, 36));
        colorPalette.put(113, new Color(132, 44, 44));
        colorPalette.put(114, new Color(153, 51, 51));
        colorPalette.put(115, new Color(81, 27, 27));
        colorPalette.put(116, new Color(17, 17, 17));
        colorPalette.put(117, new Color(21, 21, 21));
        colorPalette.put(118, new Color(25, 25, 25));
        colorPalette.put(119, new Color(13, 13, 13));
        colorPalette.put(120, new Color(176, 168, 54));
        colorPalette.put(121, new Color(215, 205, 66));
        colorPalette.put(122, new Color(250, 238, 77));
        colorPalette.put(123, new Color(132, 126, 40));
        colorPalette.put(124, new Color(64, 154, 150));
        colorPalette.put(125, new Color(79, 188, 183));
        colorPalette.put(126, new Color(92, 219, 213));
        colorPalette.put(127, new Color(48, 115, 112));
        colorPalette.put(128, new Color(52, 90, 180));
        colorPalette.put(129, new Color(63, 110, 220));
        colorPalette.put(130, new Color(74, 128, 255));
        colorPalette.put(131, new Color(39, 67, 135));
        colorPalette.put(132, new Color(0, 153, 40));
        colorPalette.put(133, new Color(0, 187, 50));
        colorPalette.put(134, new Color(0, 217, 58));
        colorPalette.put(135, new Color(0, 114, 30));
        colorPalette.put(136, new Color(91, 60, 34));
        colorPalette.put(137, new Color(111, 74, 42));
        colorPalette.put(138, new Color(129, 86, 49));
        colorPalette.put(139, new Color(68, 45, 25));
        colorPalette.put(140, new Color(79, 1, 0));
        colorPalette.put(141, new Color(96, 1, 0));
        colorPalette.put(142, new Color(112, 2, 0));
        colorPalette.put(143, new Color(59, 1, 0));
        colorPalette.put(144, new Color(147, 124, 113));
        colorPalette.put(145, new Color(180, 152, 138));
        colorPalette.put(146, new Color(209, 177, 161));
        colorPalette.put(147, new Color(110, 93, 85));
        colorPalette.put(148, new Color(112, 57, 25));
        colorPalette.put(149, new Color(137, 70, 31));
        colorPalette.put(150, new Color(159, 82, 36));
        colorPalette.put(151, new Color(84, 43, 19));
        colorPalette.put(152, new Color(105, 61, 76));
        colorPalette.put(153, new Color(128, 75, 93));
        colorPalette.put(154, new Color(149, 87, 108));
        colorPalette.put(155, new Color(78, 46, 57));
        colorPalette.put(156, new Color(79, 76, 97));
        colorPalette.put(157, new Color(96, 93, 119));
        colorPalette.put(158, new Color(112, 108, 138));
        colorPalette.put(159, new Color(59, 57, 73));
        colorPalette.put(160, new Color(131, 93, 25));
        colorPalette.put(161, new Color(160, 114, 31));
        colorPalette.put(162, new Color(186, 133, 36));
        colorPalette.put(163, new Color(98, 70, 19));
        colorPalette.put(164, new Color(72, 82, 37));
        colorPalette.put(165, new Color(88, 100, 45));
        colorPalette.put(166, new Color(103, 117, 53));
        colorPalette.put(167, new Color(54, 61, 28));
        colorPalette.put(168, new Color(112, 54, 55));
        colorPalette.put(169, new Color(138, 66, 67));
        colorPalette.put(170, new Color(160, 77, 78));
        colorPalette.put(171, new Color(84, 40, 41));
        colorPalette.put(172, new Color(40, 28, 24));
        colorPalette.put(173, new Color(49, 35, 30));
        colorPalette.put(174, new Color(57, 41, 35));
        colorPalette.put(175, new Color(30, 21, 18));
        colorPalette.put(176, new Color(95, 75, 69));
        colorPalette.put(177, new Color(116, 92, 84));
        colorPalette.put(178, new Color(135, 107, 98));
        colorPalette.put(179, new Color(71, 56, 51));
        colorPalette.put(180, new Color(61, 64, 64));
        colorPalette.put(181, new Color(75, 79, 79));
        colorPalette.put(182, new Color(87, 92, 92));
        colorPalette.put(183, new Color(46, 48, 48));
        colorPalette.put(184, new Color(86, 51, 62));
        colorPalette.put(185, new Color(105, 62, 75));
        colorPalette.put(186, new Color(122, 73, 88));
        colorPalette.put(187, new Color(64, 38, 46));
        colorPalette.put(188, new Color(53, 43, 64));
        colorPalette.put(189, new Color(65, 53, 79));
        colorPalette.put(190, new Color(76, 62, 92));
        colorPalette.put(191, new Color(40, 32, 48));
        colorPalette.put(192, new Color(53, 35, 24));
        colorPalette.put(193, new Color(65, 43, 30));
        colorPalette.put(194, new Color(76, 50, 35));
        colorPalette.put(195, new Color(40, 26, 18));
        colorPalette.put(196, new Color(53, 57, 29));
        colorPalette.put(197, new Color(65, 70, 36));
        colorPalette.put(198, new Color(76, 82, 42));
        colorPalette.put(199, new Color(40, 43, 22));
        colorPalette.put(200, new Color(100, 42, 32));
        colorPalette.put(201, new Color(122, 51, 39));
        colorPalette.put(202, new Color(142, 60, 46));
        colorPalette.put(203, new Color(75, 31, 24));
        colorPalette.put(204, new Color(26, 15, 11));
        colorPalette.put(205, new Color(31, 18, 13));
        colorPalette.put(206, new Color(37, 22, 16));
        colorPalette.put(207, new Color(19, 11, 8));
    }

    @Override
    public void runCommand(String s, String[] args) {
        if (mc.world == null || mc.world.getMapStorage() == null) return;

        Path mapDirRoot = FileManager.CATALYST_DIR.toPath().resolve("Maps");
        mapDirRoot.toFile().mkdirs();

        // server map sub dir
        String serverIdentifier;
        if (mc.isIntegratedServerRunning()) {
            serverIdentifier = "singleplayer";
        } else if (mc.getCurrentServerData() != null && !mc.getCurrentServerData().serverIP.isEmpty()) {
            serverIdentifier = mc.getCurrentServerData().serverIP;
        } else {
            serverIdentifier = "unknown";
        }
        Path mapDirServer = mapDirRoot.resolve(serverIdentifier);
        mapDirServer.toFile().mkdirs();

        mc.world.getMapStorage().loadedDataMap.values().forEach(d -> dump(d, mapDirServer));
        ChatUtils.message("Dumped " + mc.world.getMapStorage().loadedDataMap.values().size() + " maps to " + mapDirServer);
    }

    private void dump(WorldSavedData data, Path dir) {

        String name = data.mapName;

        // nbt
        Path path = dir.resolve(name + ".txt");
        NBTTagCompound nbt = data.serializeNBT();
        try {
            Files.createFile(path);
            FileWriter writer = new FileWriter(path.toFile());
            writer.write(nbt.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // png
        byte[] mapColorData = nbt.getByteArray("colors");
        int width = nbt.getInteger("width");
        int height = nbt.getInteger("height");
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int pixelPointer = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, colorPalette.get(mapColorData[pixelPointer] & 0xff).getRGB());
                pixelPointer++;
            }
        }
        try {
            image = ImageUtils.flip(image);
            image = ImageUtils.rotate(image, 90);
            File fileName = dir.resolve(name + ".png").toFile();
            ImageIO.write(image, "png", fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getDescription() {
        return "Dumps loaded maps to disk";
    }

    @Override
    public String getSyntax() {
        return "mapdump";
    }

}*/
