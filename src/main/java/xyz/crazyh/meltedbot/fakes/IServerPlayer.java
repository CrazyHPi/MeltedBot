package xyz.crazyh.meltedbot.fakes;


import xyz.crazyh.meltedbot.helpers.EntityPlayerActionPack;

public interface IServerPlayer
{
    EntityPlayerActionPack getActionPack();
    void invalidateEntityObjectReference();
    boolean isInvalidEntityObject();
    String getLanguage();
}
