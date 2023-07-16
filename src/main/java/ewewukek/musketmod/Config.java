package ewewukek.musketmod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Locale;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {
    private static final Logger logger = LogManager.getLogger(MusketMod.class);
    public static final Config INSTANCE = new Config();
    public static final int VERSION = 2;

    //General values
    public double bulletMaxDistance;
    //Musket values
    public double musketBulletStdDev;
    public double musketBulletSpeed;
    public double musketDamageMin;
    public double musketDamageMax;
    public double musketReloadDuration;
    //Rifle values
    public double rifleBulletStdDev;
    public double rifleBulletSpeed;
    public double rifleDamageMin;
    public double rifleDamageMax;
    public double rifleReloadDuration;
    //Pistol values
    public double pistolBulletStdDev;
    public double pistolBulletSpeed;
    public double pistolDamageMin;
    public double pistolDamageMax;
    public double pistolReloadDuration;
    //Blunderbuss values
    public double blunderbussBulletStdDev;
    public double blunderbussBulletSpeed;
    public double blunderbussPelletCount;
    public double blunderbussDamageMin;
    public double blunderbussDamageMax;
    public double blunderbussReloadDuration;

    public static void reload() {
        INSTANCE.setDefaults();
        INSTANCE.load();

        BulletEntity.maxDistance = INSTANCE.bulletMaxDistance;

        MusketItem.bulletStdDev = (float)Math.toRadians(INSTANCE.musketBulletStdDev);
        MusketItem.bulletSpeed = (float)(INSTANCE.musketBulletSpeed / 20);
        MusketItem.pelletCount = 1;
        double maxEnergy = MusketItem.bulletSpeed * MusketItem.bulletSpeed;
        MusketItem.damageMultiplierMin = (float)(INSTANCE.musketDamageMin / maxEnergy);
        MusketItem.damageMultiplierMax = (float)(INSTANCE.musketDamageMax / maxEnergy);
        MusketItem.reloadDuration = (int)INSTANCE.musketReloadDuration;

        RifleItem.bulletStdDev = (float)Math.toRadians(INSTANCE.rifleBulletStdDev);
        RifleItem.bulletSpeed = (float)(INSTANCE.rifleBulletSpeed / 20);
        RifleItem.pelletCount = 1;
        maxEnergy = RifleItem.bulletSpeed * RifleItem.bulletSpeed;
        RifleItem.damageMultiplierMin = (float)(INSTANCE.rifleDamageMax / maxEnergy);
        RifleItem.damageMultiplierMax = (float)(INSTANCE.rifleDamageMax / maxEnergy);
        RifleItem.reloadDuration = (int)INSTANCE.rifleReloadDuration;

        PistolItem.bulletStdDev = (float)Math.toRadians(INSTANCE.pistolBulletStdDev);
        PistolItem.bulletSpeed = (float)(INSTANCE.pistolBulletSpeed / 20);
        PistolItem.pelletCount = 1;
        maxEnergy = PistolItem.bulletSpeed * PistolItem.bulletSpeed;
        PistolItem.damageMultiplierMin = (float)(INSTANCE.pistolDamageMin / maxEnergy);
        PistolItem.damageMultiplierMax = (float)(INSTANCE.pistolDamageMax / maxEnergy);
        PistolItem.reloadDuration = (int)INSTANCE.pistolReloadDuration;

        BlunderbussItem.bulletStdDev = (float)Math.toRadians(INSTANCE.blunderbussBulletStdDev);
        BlunderbussItem.bulletSpeed = (float)(INSTANCE.blunderbussBulletSpeed / 20);
        BlunderbussItem.pelletCount = (int)(INSTANCE.blunderbussPelletCount);
        maxEnergy = BlunderbussItem.bulletSpeed * BlunderbussItem.bulletSpeed;
        BlunderbussItem.damageMultiplierMin = (float)(INSTANCE.blunderbussDamageMin / maxEnergy);
        BlunderbussItem.damageMultiplierMax = (float)(INSTANCE.blunderbussDamageMax / maxEnergy);
        BlunderbussItem.reloadDuration = (int)INSTANCE.blunderbussReloadDuration;

        logger.info("Configuration has been loaded");
    }

    private void setDefaults() {
        //General values
        bulletMaxDistance = 256;
        //Musket values
        musketBulletStdDev = 1;
        musketBulletSpeed = 220;
        musketDamageMin = 27.5;
        musketDamageMax = 28;
        musketReloadDuration = 30;
        //Rifle values
        rifleBulletStdDev = 0.2;
        rifleBulletSpeed = 230;
        rifleDamageMin = 27;
        rifleDamageMax = 28;
        rifleReloadDuration = 40;
        //Pistol values
        pistolBulletStdDev = 1.5;
        pistolBulletSpeed = 140;
        pistolDamageMin = 14;
        pistolDamageMax = 15;
        pistolReloadDuration = 24;
        //Blunderbuss values
        blunderbussBulletStdDev = 4;
        blunderbussBulletSpeed = 100;
        blunderbussPelletCount = 12;
        blunderbussDamageMin = 9;
        blunderbussDamageMax = 11;
        blunderbussReloadDuration = 24; //240
    }

    private void load() {
        int version = 0;
        try (BufferedReader reader = Files.newBufferedReader(MusketMod.CONFIG_PATH)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                int commentStart = line.indexOf('#');
                if (commentStart != -1) line = line.substring(0, commentStart);

                line.trim();
                if (line.length() == 0) continue;

                String errorPrefix = MusketMod.CONFIG_PATH+": line "+lineNumber+": ";
                try (Scanner s = new Scanner(line)) {
                    s.useLocale(Locale.US);
                    s.useDelimiter("\\s*=\\s*");

                    if (!s.hasNext()) {
                        logger.warn(errorPrefix+"parameter name is missing");
                        continue;
                    }
                    String key = s.next().trim();

                    if (!s.hasNextDouble()) {
                        logger.warn(errorPrefix+"value is missing/wrong/not a number");
                        continue;
                    }
                    double value = s.nextDouble();

                    switch (key) {
                    //mod version
                    case "version":
                        version = (int)value;
                        break;
                    //general values
                    case "bulletMaxDistance":
                        bulletMaxDistance = value;
                        break;
                    //musket values
                    case "musketBulletStdDev":
                        musketBulletStdDev = value;
                        break;
                    case "musketBulletSpeed":
                        musketBulletSpeed = value;
                        break;
                    case "musketDamageMin":
                        musketDamageMin = value;
                        break;
                    case "musketDamageMax":
                        musketDamageMax = value;
                        break;
                    case "musketReloadDuration":
                        musketReloadDuration = value;
                        break;
                    //rifle values
                    case "rifleBulletStdDev":
                        rifleBulletStdDev = value;
                        break;
                    case "rifleBulletSpeed":
                        rifleBulletSpeed = value;
                        break;
                    case "rifleDamageMin":
                        rifleDamageMin = value;
                        break;
                    case "rifleDamageMax":
                        rifleDamageMax = value;
                        break;
                    case "rifleReloadDuration":
                        rifleReloadDuration = value;
                        break;
                    //Pistol values
                    case "pistolBulletStdDev":
                        pistolBulletStdDev = value;
                        break;
                    case "pistolBulletSpeed":
                        pistolBulletSpeed = value;
                        break;
                    case "pistolDamageMin":
                        pistolDamageMin = value;
                        break;
                    case "pistolDamageMax":
                        pistolDamageMax = value;
                        break;
                    case "pistolReloadDuration":
                        pistolReloadDuration = value;
                        break;
                    //Blunderbuss values
                    case "blunderbussBulletStdDev":
                        blunderbussBulletStdDev = value;
                        break;
                    case "blunderbussBulletSpeed":
                        blunderbussBulletSpeed = value;
                        break;
                    case "blunderbussPelletCount":
                        blunderbussPelletCount = value;
                        break;
                    case "blunderbussDamageMin":
                        blunderbussDamageMin = value;
                        break;
                    case "blunderbussDamageMax":
                        blunderbussDamageMax = value;
                        break;
                    case "blunderbussReloadDuration":
                        blunderbussReloadDuration = value;
                        break;
                    default:
                        logger.warn(errorPrefix+"unrecognized parameter name: "+key);
                    }
                }
            }
        } catch (NoSuchFileException e) {
            save();
            logger.info("Configuration file not found, default created");

        } catch (IOException e) {
            logger.warn("Could not read configuration file: ", e);
        }
        if (version < VERSION) {
            logger.info("Configuration file belongs to older version, updating");
            if (version < 2) {
                if (musketDamageMax == 21.5) musketDamageMax = 21;
            }
            save();
        }
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(MusketMod.CONFIG_PATH)) {
            writer.write("version = "+VERSION+"\n");
            writer.write("\n");
            writer.write("# Maximum bullet travel distance (in blocks)\n");
            writer.write("bulletMaxDistance = "+bulletMaxDistance+"\n");
            writer.write("\n");
            writer.write("# Musket\n");
            writer.write("\n");
            writer.write("# Standard deviation of bullet spread (in degrees)\n");
            writer.write("musketBulletStdDev = "+ musketBulletStdDev +"\n");
            writer.write("# Muzzle velocity of bullet (in blocks per second)\n");
            writer.write("musketBulletSpeed = "+ musketBulletSpeed +"\n");
            writer.write("# Minimum damage at point-blank range\n");
            writer.write("musketDamageMin = "+ musketDamageMin +"\n");
            writer.write("# Maximum damage at point-blank range\n");
            writer.write("musketDamageMax = "+ musketDamageMax +"\n");
            writer.write("# Reload duration\n");
            writer.write("musketReloadDuration = "+ musketReloadDuration +"\n");
            writer.write("\n");
            writer.write("# Rifle\n");
            writer.write("\n");
            writer.write("# Standard deviation of bullet spread (in degrees)\n");
            writer.write("rifleBulletStdDev = "+ rifleBulletStdDev +"\n");
            writer.write("# Muzzle velocity of bullet (in blocks per second)\n");
            writer.write("rifleBulletSpeed = "+ rifleBulletSpeed +"\n");
            writer.write("# Minimum damage at point-blank range\n");
            writer.write("rifleDamageMin = "+ rifleDamageMin +"\n");
            writer.write("# Maximum damage at point-blank range\n");
            writer.write("rifleDamageMax = "+ rifleDamageMax +"\n");
            writer.write("# Reload duration\n");
            writer.write("rifleReloadDuration = "+ rifleReloadDuration +"\n");
            writer.write("\n");
            writer.write("# Pistol\n");
            writer.write("\n");
            writer.write("# Standard deviation of bullet spread (in degrees)\n");
            writer.write("pistolBulletStdDev = "+pistolBulletStdDev+"\n");
            writer.write("# Muzzle velocity of bullet (in blocks per second)\n");
            writer.write("pistolBulletSpeed = "+pistolBulletSpeed+"\n");
            writer.write("# Minimum damage at point-blank range\n");
            writer.write("pistolDamageMin = "+pistolDamageMin+"\n");
            writer.write("# Maximum damage at point-blank range\n");
            writer.write("pistolDamageMax = "+pistolDamageMax+"\n");
            writer.write("# Reload duration\n");
            writer.write("pistolReloadDuration = "+pistolReloadDuration+"\n");
            writer.write("# Blunderbuss\n");
            writer.write("\n");
            writer.write("# Standard deviation of bullet spread (in degrees)\n");
            writer.write("blunderbussBulletStdDev = "+blunderbussBulletStdDev+"\n");
            writer.write("# Muzzle velocity of bullet (in blocks per second)\n");
            writer.write("blunderbussBulletSpeed = "+blunderbussBulletSpeed+"\n");
            writer.write("# Number of projectiles fired in one shot\n");
            writer.write("blunderbussPelletCount = "+blunderbussPelletCount+"\n");
            writer.write("# Minimum damage at point-blank range\n");
            writer.write("blunderbussDamageMin = "+blunderbussDamageMin+"\n");
            writer.write("# Maximum damage at point-blank range\n");
            writer.write("blunderbussDamageMax = "+blunderbussDamageMax+"\n");
            writer.write("# Reload duration\n");
            writer.write("blunderbussReloadDuration = "+blunderbussReloadDuration+"\n");

        } catch (IOException e) {
            logger.warn("Could not save configuration file: ", e);
        }
    }
}
