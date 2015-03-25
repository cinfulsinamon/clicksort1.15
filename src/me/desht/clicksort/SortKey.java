package me.desht.clicksort;

import java.util.Map;
import java.util.Map.Entry;

import me.desht.dhutils.LogUtils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SortKey implements Comparable<SortKey>
{
    private final String sortPrefix;
    private final Material material;
    private final short durability;
    private final String metaStr;
    private final ItemMeta meta;
    
    public SortKey(ItemStack stack, SortingMethod sortMethod)
    {
        String prefix = sortMethod.makeSortPrefix(stack);
        if (prefix == null)
        {
            this.sortPrefix = stack.getType().toString();
            LogUtils.warning("Can't determine sort prefix for " + stack + " (using "
                    + this.sortPrefix + ")");
        }
        else
        {
            this.sortPrefix = prefix;
        }
        this.material = stack.getType();
        this.durability = stack.getDurability();
        this.meta = stack.getItemMeta();
        this.metaStr = makeMetaString();
    }
    
    /**
     * @return the sortPrefix
     */
    public String getSortPrefix()
    {
        return sortPrefix;
    }
    
    /**
     * @return the materialID
     */
    public Material getMaterial()
    {
        return material;
    }
    
    /**
     * @return the durability
     */
    public short getDurability()
    {
        return durability;
    }
    
    /**
     * @return the metaStr
     */
    public String getMetaStr()
    {
        return metaStr;
    }
    
    public ItemStack toItemStack(int amount)
    {
        ItemStack stack = new ItemStack(getMaterial(), amount, getDurability());
        stack.setItemMeta(meta);
        return stack;
    }
    
    @Override
    public int compareTo(SortKey other)
    {
        if (other == null)
            return 1;
        
        int c = this.getSortPrefix().compareTo(other.getSortPrefix());
        if (c != 0)
            return c;
        
        // the Material enum members are arranged by item ID
        c = this.getMaterial().ordinal() - other.getMaterial().ordinal();
        if (c != 0)
            return c;
        
        c = this.getDurability() - other.getDurability();
        if (c != 0)
            return c;
        
        return this.getMetaStr().compareTo(other.getMetaStr());
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        
        SortKey sortKey = (SortKey) o;
        
        if (durability != sortKey.durability)
            return false;
        if (material != sortKey.material)
            return false;
        if (meta != null ? !meta.equals(sortKey.meta) : sortKey.meta != null)
            return false;
        if (!metaStr.equals(sortKey.metaStr))
            return false;
        if (!sortPrefix.equals(sortKey.sortPrefix))
            return false;
        
        return true;
    }
    
    @Override
    public int hashCode()
    {
        int result = sortPrefix.hashCode();
        result = 31 * result + material.hashCode();
        result = 31 * result + durability;
        result = 31 * result + metaStr.hashCode();
        result = 31 * result + (meta != null ? meta.hashCode() : 0);
        return result;
    }
    
    private String makeMetaString()
    {
        if (meta == null)
        {
            return "";
        }
        Map<String, Object> map = meta.serialize();
        
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Object> entry : map.entrySet())
        {
            sb.append(entry.getKey()).append("=").append(entry.getValue().toString())
                    .append(";");
        }
        return sb.toString();
    }
    
    @Override
    public String toString()
    {
        return String.format("SortKey[%s|%s|%d|%s]", getSortPrefix(), getMaterial()
                .toString(), getDurability(), getMetaStr());
    }
}
