package com.mairwunnx.projectessentials

import com.mairwunnx.projectessentials.commands.*
import com.mairwunnx.projectessentials.commands.teleport.TopCommand
import com.mairwunnx.projectessentials.commands.teleport.TpPosCommand
import com.mairwunnx.projectessentials.configurations.KitsConfiguration
import com.mairwunnx.projectessentials.configurations.UserDataConfiguration

internal val providers = listOf(
    UserDataConfiguration::class.java,
    KitsConfiguration::class.java,
    ModuleObject::class.java,
    AfkCommand::class.java,
    AirCommand::class.java,
    BurnCommand::class.java,
    FeedCommand::class.java,
    HealCommand::class.java,
    FlyCommand::class.java,
    GodCommand::class.java,
    HelpCommand::class.java,
    LightningCommand::class.java,
    MoreCommand::class.java,
    PingCommand::class.java,
    RepairCommand::class.java,
    SendPosCommand::class.java,
    SuicideCommand::class.java,
    KitCommand::class.java,
    EnderChestCommand::class.java,
    InvSeeCommand::class.java,
    WorkbenchCommand::class.java,
    GlowCommand::class.java,
    SkullCommand::class.java,
    VanishCommand::class.java,
    PlatformStatusCommand::class.java,
    BreakCommand::class.java,
    JumpCommand::class.java,
    ExtCommand::class.java,
    TopCommand::class.java,
    TpPosCommand::class.java
)
