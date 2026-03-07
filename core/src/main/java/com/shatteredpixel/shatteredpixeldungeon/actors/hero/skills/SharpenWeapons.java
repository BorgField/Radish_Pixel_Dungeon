package com.shatteredpixel.shatteredpixeldungeon.actors.hero.skills;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class SharpenWeapons extends InventoryClericSpell {

    public static final SharpenWeapons INSTANCE = new SharpenWeapons();
    private Item selectedItem;
    private boolean isWeapon;

    @Override
    public int icon() {
        return HeroIcon.BATTLEMAGE;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return (item instanceof Weapon || item instanceof Armor) && item.isIdentified();
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.MOONLIGHT_T1_2);
    }

    @Override
    protected void onItemSelected(Hero hero, Item item) {
        if (item == null) return;

        // 第一次选择消耗物品
        if (selectedItem == null) {
            selectedItem = item;
            isWeapon = item instanceof Weapon;
            showTargetSelection(hero);
        }
    }

    private void showTargetSelection(Hero hero) {
        ArrayList<Item> candidates = findUnidentifiedItems(hero);

        if (candidates.isEmpty()) {
            GLog.w(Messages.get(this, "no_target"));
            resetSelection();
            return;
        }

        GameScene.selectItem(new WndBag.ItemSelector() {
            @Override
            public String textPrompt() {
                return Messages.get(SharpenWeapons.this, "prompt");
            }

            @Override
            public boolean itemSelectable(Item item) {
                return true;
            }

            @Override
            public void onSelect(Item item) {
                processTargetSelection(item);
            }
        });

    }

    private ArrayList<Item> findUnidentifiedItems(Hero hero) {
        ArrayList<Item> candidates = new ArrayList<>();
        for (Item i : hero.belongings) {
            if (i.isIdentified()) continue;

            if (isWeapon && i instanceof Weapon) {
                candidates.add(i);
            } else if (!isWeapon && i instanceof Armor) {
                candidates.add(i);
            }
        }
        return candidates;
    }

    private void processTargetSelection(Item target) {
        if (target == null) {
            resetSelection();
            return;
        }

        Hero hero = Dungeon.hero;

        // 执行鉴定效果
        identifyItem(hero, target);

        // 消耗选中的物品
        selectedItem.detach(hero.belongings.backpack);

        // 完成施法
        onSpellCast(hero);

        // 重置选择状态
        resetSelection();
    }

    private void identifyItem(Hero hero, Item item) {
        item.identify();

        hero.spend(1f);
        hero.busy();
        hero.sprite.operate(hero.pos);
        hero.sprite.parent.add(new Identification(hero.sprite.center().offset(0, -16)));
        Sample.INSTANCE.play(Assets.Sounds.READ);
    }

    private void resetSelection() {
        selectedItem = null;
        isWeapon = false;
    }
}
