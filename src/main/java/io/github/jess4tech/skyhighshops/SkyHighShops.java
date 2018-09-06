package io.github.jess4tech.skyhighshops;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Plugin(id = PluginInfo.ID, name = PluginInfo.NAME, version = PluginInfo.VERSION, description = PluginInfo.DESCRIPTION, authors = {"JessGaming", "KileyK", "Kitten160000"})

public class SkyHighShops {
    /**
     * It does what the name says it does
     * @param time How long it will pause the plugin for
     */
    public static void pauseFor(int time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException error) {
            error.printStackTrace();
        }
    }

    @Inject
    Game game;

    @Inject
    Logger logger;

    @Listener
    public void PreInit(GamePreInitializationEvent e) {
        logger.info("Sky High Shops, version " + PluginInfo.VERSION + ", is initializing!");
        pauseFor(1);
        logger.info("Verifying currency...");
        pauseFor(1);
        logger.info("Registering commands...");
        CommandInfo.registerCommands();
        pauseFor(2);
        logger.info("Sky High Shops has loaded!");
    }


    @Listener
    public void PostInit(GamePostInitializationEvent e) {
        Optional<EconomyService> optionalEconomyService = Sponge.getServiceManager().provide(EconomyService.class);
        EconomyService economyService;
        if (!optionalEconomyService.isPresent()) {
            logger.error("No economy plugin was found! In order for SkyHighShops to function you must have an economy plugin installed!");
            return;
        } else {
            economyService = optionalEconomyService.get();
        }
        CommandSpec CreditsCmd = CommandSpec.builder().description(Text.of(TextColors.GRAY, "Displays the credits and help information")).executor(new CommandExecutor() {
            @Override
            public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                src.sendMessage(Text.of(TextColors.WHITE, "This plugin was developed by Jess Gaming. Report any issues on the github repository!"));
                return CommandResult.success();
            }
        }).build();
        CommandSpec ShopCreateCmd = CommandSpec.builder()
                .arguments(GenericArguments.integer(Text.of(TextColors.GRAY, "price")))
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
                        Integer price = (int) args.getOne(Text.of("price")).orElseThrow(() -> new CommandException(Text.of("You must specify a valid price!")));
                        if (src instanceof Player) {
                            Player player = (Player) src;
                            Optional<ItemStack> itemStack = player.getItemInHand(HandTypes.MAIN_HAND);
                            if (!itemStack.isPresent() || itemStack.get().isEmpty()) {
                                player.sendMessage(Text.of(TextColors.RED, "ERR: No item in hand"));
                            } else {
                                if(price > 1) {
                                    Text itIs = Text.builder(player.getName() + ", you just sold " + itemStack + " for " + price + " ").append(
                                            economyService.getDefaultCurrency().getPluralDisplayName()).append(Text.builder("!").build()).color(TextColors.GREEN).build();
                                    player.sendMessage(itIs);
                                } else {
                                    Text itIs = Text.builder(player.getName() + ", you just sold " + itemStack + " for " + price + " ").append(
                                            economyService.getDefaultCurrency().getDisplayName()).append(Text.builder("!").build()).color(TextColors.GREEN).build();
                                    player.sendMessage(itIs);
                                }
                                player.setItemInHand(HandTypes.MAIN_HAND, ItemStack.empty());
                            }
                        } else {
                            src.sendMessage(Text.of(TextColors.RED, "Only players can use this command!"));
                        }
                        return CommandResult.success();
                    }
                }).build();
        CommandSpec ShopDestroyCmd = CommandSpec.builder()
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) {
                        if (!(src instanceof Player)) {
                            logger.warn("Non player tried to execute /shop destroy");
                        } else {
                            logger.info("Player, " + src.getName() + ", destroyed a shop");
                            src.sendMessage(Text.of("Shop successfully destroyed!"));
                        }
                        return CommandResult.success();
                    }
                })
                .build();
        CommandSpec ShopCmd = CommandSpec.builder()
                .description(Text.of(TextColors.GRAY, "Manage Shops"))
                .permission("skyhighshops.commands.shop")
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) {
                        logger.error("ERROR: No command under the name of" + null + "was found!");
                        src.sendMessage(Text.of(TextColors.RED, "FATAL ERROR: No command was provided!"));
                        return CommandResult.empty();
                    }
                })
                .child(ShopCreateCmd, "create")
                .child(ShopDestroyCmd, "destroy")
                .build();
        CommandSpec currencyList = CommandSpec.builder()
                .description(Text.of(TextColors.GRAY, "List the currency that Sky High Shops is currently using"))
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) {
                        Text currentCurrencyPlural = Text.builder("Plural: ").append(economyService.getDefaultCurrency().getPluralDisplayName()).build();
                        Text currentCurrencySingular = Text.builder("Singular").append(economyService.getDefaultCurrency().getDisplayName()).build();
                        src.sendMessage(currentCurrencyPlural);
                        src.sendMessage(currentCurrencySingular);
                        return CommandResult.success();
                    }
                })
                .build();
        CommandSpec skyList = CommandSpec.builder()
                .description(Text.of(TextColors.GRAY, "List various things relating to Sky High Shops"))
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) {
                        logger.error("ERROR: No command under the name of" + null + "was found!");
                        src.sendMessage(Text.of(TextColors.RED, "FATAL ERROR: No sub-command was provided!"));
                        return CommandResult.empty();
                    }
                })
                .child(currencyList, "currency")
                .build();
        CommandSpec skyHelp = CommandSpec.builder()
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) {
                        src.sendMessage(Text.of("Current commands:"));
                        for(String str : CommandInfo.commands) {
                            src.sendMessage(Text.of(str));
                        }
                        return CommandResult.success();
                    }
                })
                .build();
        CommandSpec sky = CommandSpec.builder()
                .description(Text.of("Various commands for Sky High Shops"))
                .permission("skyhighshops.commands.sky")
                .executor(new CommandExecutor() {
                    @Override
                    public CommandResult execute(CommandSource src, CommandContext args) {
                        src.sendMessage(Text.of("Run \"/sky help\" for more information! "));
                        return CommandResult.empty();
                    }
                })
                .child(skyList, "list")
                .child(skyHelp, "help")
                .build();
        game.getCommandManager().register(this, CreditsCmd, "credits");
        game.getCommandManager().register(this, ShopCmd, "shop");
        game.getCommandManager().register(this, sky, "sky");
    }
}

