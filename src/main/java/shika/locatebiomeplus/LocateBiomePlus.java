package shika.locatebiomeplus;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.LocateBiomeCommand;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class LocateBiomePlus implements ModInitializer {

    private static final SimpleCommandExceptionType INVALID_NUMBER_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.locatebiomeplus.invalidNumberException"));
    private static final SimpleCommandExceptionType UNFUNNY_NUMBER_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.locatebiomeplus.unfunnyNumberException"));
    private static final SimpleCommandExceptionType OFFENSIVELY_UNFUNNY_NUMBER_EXCEPTION = new SimpleCommandExceptionType(new TranslatableText("commands.locatebiomeplus.offensivelyUnfunnyNumberException"));

    private static final Dynamic2CommandExceptionType NOT_FOUND_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> {
        return new TranslatableText("commands.locatebiomeplus.notFound", new Object[]{object, object2});
    });

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("locatebiome+").requires((serverCommandSource) -> {
            return serverCommandSource.hasPermissionLevel(2);
        }).then(CommandManager.argument("biome", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.ALL_BIOMES)
                .executes((context -> {
                    return execute((ServerCommandSource)context.getSource(), (Identifier)context.getArgument("biome", Identifier.class), 6400);
                }))
                .then(CommandManager.argument("range", IntegerArgumentType.integer())
                        .executes((context) -> {
            return execute((ServerCommandSource)context.getSource(), (Identifier)context.getArgument("biome", Identifier.class), (Integer)context.getArgument("range", Integer.class));
        })))));
    }

    private static int execute(ServerCommandSource source, Identifier identifier, Integer integer) throws CommandSyntaxException {
        Biome biome = (Biome)source.getMinecraftServer().getRegistryManager().get(Registry.BIOME_KEY).getOrEmpty(identifier).orElseThrow(() -> {
            return LocateBiomeCommand.INVALID_EXCEPTION.create(identifier);
        });
        if (integer < 0) throw INVALID_NUMBER_EXCEPTION.create();
        if (integer == 69 || integer == 420) throw UNFUNNY_NUMBER_EXCEPTION.create();
        if (integer == 69420 || integer == 42069 || integer == 42069420 || integer == 6942069 || integer == 489) throw OFFENSIVELY_UNFUNNY_NUMBER_EXCEPTION.create();

        BlockPos blockPos = new BlockPos(source.getPosition());
        BlockPos blockPos2 = source.getWorld().locateBiome(biome, blockPos, integer, 8);
        String string = identifier.toString();
        if (blockPos2 == null) {
            throw NOT_FOUND_EXCEPTION.create(string, integer.toString());
        } else {
            return LocateCommand.sendCoordinates(source, string, blockPos, blockPos2, "commands.locatebiome.success");
        }
    }
}
