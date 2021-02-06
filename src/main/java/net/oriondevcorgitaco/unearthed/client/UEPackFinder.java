package net.oriondevcorgitaco.unearthed.client;

import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;

import java.io.File;
import java.util.function.Consumer;

public class UEPackFinder implements IPackFinder {
    private File folder;

    public UEPackFinder(File file) {
        this.folder = file;
    }

    @Override
    public void findPacks(Consumer<ResourcePackInfo> consumer, ResourcePackInfo.IFactory packInfoFactory) {
        if (folder.exists() && folder.isDirectory()) {
            ResourcePackInfo t = ResourcePackInfo.createResourcePack("Unearthed Generated Assets", true, () -> new FolderPack(folder), packInfoFactory, ResourcePackInfo.Priority.TOP, IPackNameDecorator.PLAIN);
            if (t == null) return;
            consumer.accept(t);
        }
    }
}
