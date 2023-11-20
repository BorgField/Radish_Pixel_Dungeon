package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class Seekingspear extends MeleeWeapon{
    //private static ItemSprite.Glowing RED = new ItemSprite.Glowing( 0x660022 );
    {
        image = ItemSpriteSheet.SEEKING;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 0.9f;

        tier = 4;
        RCH=2;
    }
    @Override
    public int min(int lvl) {
        return  8+lvl;
    }
    @Override
    public int max(int lvl) {
        return  4*tier +    //16 base, down from 25
                lvl*(tier);   //scaling down
    }
    @Override
    public Emitter emitter() {
        if (!isShining) return super.emitter();
        Emitter emitter = new Emitter();
        emitter.pos(15f, 2);//粒子效果对应的位置
        emitter.fillTarget = false;//如果为真则分布在整个空间，为假则按pos的设置
        emitter.pour(StaffParticleFactory, 0.6f);//动画内容，动画间隔（越大播放速度越慢）
        //pour 有间隔无量，超过间隔会结束播放，burst为瞬间爆发，有量无间隔，也就是瞬间爆发直到量完
        return emitter;
    }

    private final Emitter.Factory StaffParticleFactory = new Emitter.Factory() {
        /**
         * @param emitter 目标来源
         * @param index 特效来源
         * @param x,y 位置
         */
        @Override
        public void emit( Emitter emitter, int index, float x, float y ) {
            StaffParticle c = (StaffParticle)emitter.getFirstAvailable(StaffParticle.class);
            if (c == null) {
                c = new StaffParticle();
                emitter.add(c);
            }
            c.reset(x, y);
        }
        @Override
        public boolean lightMode() {
            return false;
        }
    };


    private class StaffParticle extends PixelParticle {

        private float minSize;
        private float maxSize;
        public float sizeJitter = 0;

        public StaffParticle(){
            super();
        }

        public void reset( float x, float y ) {
            revive();

            speed.set(0);

            this.x = x;
            this.y = y;
            staffFx( this );

        }
        private void staffFx(StaffParticle particle) {
            particle.color( 0x8E236B );//改颜色
            particle.am = 0.8f;//不透明度
            particle.setLifespan(2f);//影响大小，相当于影响衰减完所需全程
            particle.speed.set(0, 8);//粒子运动速度
            particle.setSize( 0.5f, 2f);//设置粒子的最小/大尺寸
            particle.shuffleXY(1f);//随机调节起始点，以原点为中心在一个半边长为amt的正方形里
        }
        public void setSize( float minSize, float maxSize ){
            this.minSize = minSize;
            this.maxSize = maxSize;
        }

        public void setLifespan( float life ){
            lifespan = left = life;
        }

        public void shuffleXY(float amt){
            x += Random.Float(-amt, amt);
            y += Random.Float(-amt, amt);
        }

        public void radiateXY(float amt){
            float hypot = (float)Math.hypot(speed.x, speed.y);
            this.x += speed.x/hypot*amt;
            this.y += speed.y/hypot*amt;
        }

        @Override
        public void update() {
            super.update();
            size(minSize + (left / lifespan)*(maxSize-minSize) + Random.Float(sizeJitter));
        }
    }
}
