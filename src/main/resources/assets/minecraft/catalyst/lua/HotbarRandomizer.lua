instance:setModuleName('HotbarRandomizer');
instance:setDescription('Randomizes you current slot');

instance:addIntegerSetting("Delay", 5, 1, 50, "The amount of ticks that passes between changing slots");

ticksPassed = 0;

instance:addListener('onClientTickEvent');
function onClientTickEvent(event)
    print("Test");
    ticksPassed = ticksPassed + 1;
    if ticksPassed > tonumber(instance:getSetting("Delay"):getValue()) then
        mc.player.inventory.currentItem = math.random(0, 8);
        ticksPassed = 0;
    end
end
