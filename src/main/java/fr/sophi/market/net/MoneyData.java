package fr.sophi.market.net;
import net.minecraft.server.level.ServerPlayer;
import java.util.*;import java.util.concurrent.ConcurrentHashMap;
public final class MoneyData {private static final Map<UUID,Long>B=new ConcurrentHashMap<>();private MoneyData(){} public static long get(ServerPlayer p){return B.getOrDefault(p.getUUID(),0L);} public static void add(ServerPlayer p,long n){if(n>0)B.merge(p.getUUID(),n,Long::sum);}}
