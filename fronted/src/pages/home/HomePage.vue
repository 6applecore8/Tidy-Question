<template>
  <main class="home-page">
    <header class="site-nav">
      <div class="nav-shell">
        <RouterLink class="brand-link" to="/">Studio</RouterLink>

        <div class="nav-pill">
          <RouterLink class="nav-link" to="/">首页</RouterLink>
          <RouterLink class="nav-link" to="/product">产品</RouterLink>
          <RouterLink class="nav-login" to="/signin">
            登录
            <span aria-hidden="true">↗</span>
          </RouterLink>
        </div>
      </div>
    </header>

    <section class="hero-section">
      <video class="hero-video" autoplay loop muted playsinline>
        <source
          src="https://d8j0ntlcm91z4.cloudfront.net/user_38xzZboKViGWJOttwIXH07lWA1P/hf_20260314_131748_f2ca2a28-fed7-44c8-b9a9-bd9acdd5ec31.mp4"
          type="video/mp4"
        />
      </video>
      <div class="hero-overlay"></div>

      <div class="hero-content">
        <div class="hero-badge reveal reveal-1">为您的品牌量身打造</div>
        <h1 class="hero-title reveal reveal-2">智能设计，温暖呈现</h1>
        <p class="hero-copy reveal reveal-3">
          我们相信好的设计应该让人感到舒适、克制而有力量。让视频先抓住情绪，再让内容慢慢展开。
        </p>
        <div class="hero-actions reveal reveal-4">
          <RouterLink class="hero-primary" to="/signin">
            免费试用
            <span aria-hidden="true">↗</span>
          </RouterLink>
          <a class="hero-secondary" href="#features">了解更多 →</a>
        </div>
      </div>
    </section>

    <section id="features" class="section section-wide">
      <div class="section-heading centered">
        <div class="section-badge">核心功能</div>
        <h2>让创意自然生长</h2>
        <p>以视频为入口，以系统化能力为支撑，把品牌表达、设计效率与上线速度整合到同一条路径里。</p>
      </div>

      <div class="feature-grid">
        <article v-for="(item, index) in features" :key="item.title" class="glass-card reveal-card" :style="{ animationDelay: `${index * 0.1}s` }">
          <div class="card-icon">{{ item.icon }}</div>
          <h3>{{ item.title }}</h3>
          <p>{{ item.description }}</p>
        </article>
      </div>
    </section>

    <section class="section section-narrow">
      <div class="section-heading centered">
        <div class="section-badge">使用流程</div>
        <h2>三步开启旅程</h2>
      </div>

      <div class="steps-grid">
        <article v-for="(step, index) in steps" :key="step.title" class="step-card reveal-card" :style="{ animationDelay: `${index * 0.15}s` }">
          <div class="step-number">0{{ index + 1 }}</div>
          <h3>{{ step.title }}</h3>
          <p>{{ step.description }}</p>
        </article>
      </div>
    </section>

    <section class="section section-wide pricing-section">
      <div class="section-heading centered">
        <div class="section-badge">定价方案</div>
        <h2>选择适合您的方案</h2>
      </div>

      <div class="pricing-grid">
        <article v-for="plan in pricing" :key="plan.name" class="pricing-card reveal-card" :class="{ 'pricing-card--featured': plan.featured }">
          <div class="pricing-head">
            <div>
              <p class="pricing-name">{{ plan.name }}</p>
              <h3>{{ plan.price }}</h3>
            </div>
            <span v-if="plan.featured" class="popular-tag">最受欢迎</span>
          </div>

          <ul class="pricing-list">
            <li v-for="feature in plan.features" :key="feature">
              <span aria-hidden="true">✓</span>
              <span>{{ feature }}</span>
            </li>
          </ul>

          <RouterLink class="pricing-cta" :class="{ 'pricing-cta--featured': plan.featured }" to="/signin">
            {{ plan.featured ? '立即开始' : '咨询方案' }}
          </RouterLink>
        </article>
      </div>
    </section>

    <section class="section bottom-cta">
      <h2>准备好了吗？</h2>
      <p>先让视觉打动人，再让系统把效率撑起来。下一步可以从一次试用开始。</p>
      <div class="bottom-actions">
        <RouterLink class="hero-primary" to="/signin">
          免费开始
          <span aria-hidden="true">↗</span>
        </RouterLink>
        <RouterLink class="hero-secondary hero-secondary--router" to="/">返回首页</RouterLink>
      </div>
    </section>

    <footer class="site-footer">
      <span>© 2026 Studio. All rights reserved.</span>
      <div class="footer-links">
        <a href="#">隐私政策</a>
        <a href="#">服务条款</a>
        <a href="#">联系我们</a>
      </div>
    </footer>
  </main>
</template>

<script setup lang="ts">
import { nextTick, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

const features = [
  {
    icon: '✦',
    title: 'AI 智能设计',
    description: '基于深度学习的设计引擎，自动生成符合品牌调性的精美界面，并保留人工精调空间。',
  },
  {
    icon: '◫',
    title: '组件化架构',
    description: '模块化的设计系统，灵活组合、快速迭代，让复杂页面也能稳定复用。',
  },
  {
    icon: '◎',
    title: '全球化部署',
    description: '一键分发到全球节点，缩短首屏等待时间，让访问体验更平滑。',
  },
  {
    icon: '◌',
    title: '性能极致优化',
    description: '自动代码分割、媒体压缩与懒加载，让高质感页面依然保持轻快。',
  },
] as const;

const steps = [
  {
    title: '描述愿景',
    description: '告诉我们您的品牌故事、目标受众和设计偏好。',
  },
  {
    title: 'AI 创作',
    description: '智能引擎在数小时内生成多套设计方案供您选择。',
  },
  {
    title: '精调上线',
    description: '微调细节后一键发布，您的网站即刻面向全球。',
  },
] as const;

const pricing = [
  {
    name: 'Starter',
    price: '¥2,999/月',
    featured: false,
    features: ['5 个页面', 'AI 智能设计', '响应式适配', '基础 SEO', '7×24 支持'],
  },
  {
    name: 'Professional',
    price: '¥6,999/月',
    featured: true,
    features: ['无限页面', '高级 AI 定制', 'A/B 测试', '数据分析', '优先支持', '自定义域名'],
  },
  {
    name: 'Enterprise',
    price: '定制价格',
    featured: false,
    features: ['全功能', '专属顾问', 'API 集成', 'SLA', '私有化部署', '定制培训'],
  },
] as const;

async function scrollToFeaturesIfNeeded(path: string): Promise<void> {
  if (path !== '/product') {
    return;
  }

  await nextTick();
  document.getElementById('features')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

onMounted(() => {
  void scrollToFeaturesIfNeeded(route.path);
});

watch(() => route.path, (path) => {
  void scrollToFeaturesIfNeeded(path);
});
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Instrument+Serif:ital@0;1&family=Inter:wght@300;400;500;600&display=swap');

.home-page {
  --bg: #04070a;
  --fg: #f5f7fb;
  --muted: rgba(245, 247, 251, 0.56);
  --line: rgba(245, 247, 251, 0.1);
  position: relative;
  min-height: 100vh;
  background: #030609;
  color: var(--fg);
  font-family: 'Inter', sans-serif;
}

.site-nav {
  position: fixed;
  top: 16px;
  left: 0;
  right: 0;
  z-index: 50;
  padding: 0 32px;
}

.nav-shell {
  display: flex;
  align-items: center;
  justify-content: center;
  max-width: 1280px;
  margin: 0 auto;
  position: relative;
}

.brand-link {
  position: absolute;
  left: 0;
  color: var(--fg);
  font-family: 'Instrument Serif', serif;
  font-size: 2rem;
  font-style: italic;
  text-decoration: none;
}

.nav-pill {
  display: none;
}

.hero-section {
  position: relative;
  overflow: hidden;
  min-height: 700px;
}

.hero-video {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.4), rgba(0, 0, 0, 0.2), rgba(0, 0, 0, 0.92));
}

.hero-content {
  position: relative;
  z-index: 10;
  display: flex;
  min-height: 700px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 160px 24px 120px;
  text-align: center;
}

.hero-badge,
.section-badge {
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: rgba(255, 255, 255, 0.06);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.16);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.hero-badge {
  margin-bottom: 22px;
  padding: 8px 16px;
  font-size: 0.88rem;
}

.hero-title {
  max-width: 900px;
  margin: 0;
  font-family: 'Instrument Serif', serif;
  font-size: clamp(3.4rem, 8vw, 6.6rem);
  font-style: italic;
  line-height: 0.88;
  letter-spacing: -0.04em;
}

.hero-copy {
  max-width: 620px;
  margin: 28px 0 0;
  color: var(--muted);
  font-size: 0.96rem;
  font-weight: 300;
  line-height: 1.8;
}

.hero-actions,
.bottom-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 18px;
  margin-top: 34px;
}

.hero-primary,
.pricing-cta {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
  min-height: 52px;
  padding: 0 24px;
  border-radius: 999px;
  color: var(--fg);
  text-decoration: none;
  background: rgba(255, 255, 255, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(14px);
  transition: transform 180ms ease, background 180ms ease;
}

.hero-primary:hover,
.pricing-cta:hover {
  transform: scale(1.05);
  background: rgba(255, 255, 255, 0.16);
}

.hero-secondary {
  color: rgba(245, 247, 251, 0.82);
  text-decoration: none;
}

.hero-secondary--router {
  display: inline-flex;
  align-items: center;
  min-height: 52px;
}

.section {
  position: relative;
  z-index: 1;
  padding: 96px 24px;
}

.section-wide {
  max-width: 1280px;
  margin: 0 auto;
}

.section-narrow {
  max-width: 1040px;
  margin: 0 auto;
}

.section-heading {
  margin-bottom: 42px;
}

.section-heading.centered {
  text-align: center;
}

.section-badge {
  display: inline-flex;
  padding: 8px 14px;
  font-size: 0.84rem;
}

.section-heading h2,
.bottom-cta h2 {
  margin: 16px 0 0;
  font-family: 'Instrument Serif', serif;
  font-size: clamp(2.8rem, 5vw, 5rem);
  font-style: italic;
  line-height: 0.92;
  letter-spacing: -0.04em;
}

.section-heading p,
.bottom-cta p {
  max-width: 560px;
  margin: 18px auto 0;
  color: rgba(245, 247, 251, 0.5);
  font-size: 0.95rem;
  font-weight: 300;
  line-height: 1.8;
}

.feature-grid,
.pricing-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24px;
}

.glass-card,
.pricing-card,
.step-card {
  border: 1px solid var(--line);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.05);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(14px);
}

.glass-card {
  padding: 32px;
}

.card-icon {
  display: grid;
  place-items: center;
  width: 48px;
  height: 48px;
  margin-bottom: 22px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  font-size: 1.1rem;
}

.glass-card h3,
.step-card h3,
.pricing-name {
  margin: 0;
  font-size: 1.35rem;
}

.glass-card p,
.step-card p {
  margin: 14px 0 0;
  color: rgba(245, 247, 251, 0.64);
  line-height: 1.8;
}

.steps-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 28px;
}

.step-card {
  padding: 28px;
  text-align: center;
}

.step-number {
  color: rgba(245, 247, 251, 0.22);
  font-family: 'Instrument Serif', serif;
  font-size: 4.2rem;
  font-style: italic;
  line-height: 1;
}

.pricing-section {
  padding-top: 104px;
}

.pricing-card {
  display: flex;
  flex-direction: column;
  padding: 32px;
}

.pricing-card--featured {
  border-color: rgba(245, 247, 251, 0.2);
  background: rgba(255, 255, 255, 0.1);
}

.pricing-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.pricing-head h3 {
  margin: 12px 0 0;
  font-size: 2.2rem;
}

.popular-tag {
  border-radius: 999px;
  padding: 6px 12px;
  color: #071018;
  font-size: 0.76rem;
  background: #f5f7fb;
}

.pricing-list {
  display: grid;
  gap: 12px;
  padding: 0;
  margin: 28px 0;
  list-style: none;
}

.pricing-list li {
  display: flex;
  gap: 10px;
  color: rgba(245, 247, 251, 0.7);
  font-size: 0.94rem;
}

.pricing-cta {
  margin-top: auto;
}

.pricing-cta--featured {
  color: #020508;
  background: #f5f7fb;
}

.bottom-cta {
  max-width: 760px;
  margin: 0 auto;
  text-align: center;
}

.site-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px 32px;
  border-top: 1px solid rgba(245, 247, 251, 0.08);
  color: rgba(245, 247, 251, 0.4);
  font-size: 0.78rem;
}

.footer-links {
  display: flex;
  gap: 18px;
}

.footer-links a {
  color: inherit;
  text-decoration: none;
}

.reveal,
.reveal-card {
  opacity: 0;
  transform: translateY(20px);
  filter: blur(10px);
  animation: fade-rise 0.8s ease forwards;
}

.reveal-1 { animation-delay: 0.1s; }
.reveal-2 { animation-delay: 0.2s; }
.reveal-3 { animation-delay: 0.6s; }
.reveal-4 { animation-delay: 0.9s; }

@keyframes fade-rise {
  to {
    opacity: 1;
    transform: translateY(0);
    filter: blur(0);
  }
}

@media (min-width: 768px) {
  .nav-pill {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    border-radius: 999px;
    padding: 6px;
    border: 1px solid rgba(255, 255, 255, 0.14);
    background: rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(14px);
  }

  .nav-link,
  .nav-login {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    border-radius: 999px;
    padding: 10px 14px;
    color: rgba(245, 247, 251, 0.9);
    font-size: 0.92rem;
    font-weight: 500;
    text-decoration: none;
  }

  .nav-login {
    color: #020508;
    background: #f5f7fb;
  }
}

@media (max-width: 767px) {
  .site-nav {
    padding: 0 16px;
  }

  .nav-shell {
    justify-content: space-between;
  }

  .brand-link {
    position: static;
  }

  .nav-pill {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 6px;
    border-radius: 999px;
    border: 1px solid rgba(255, 255, 255, 0.14);
    background: rgba(255, 255, 255, 0.05);
    backdrop-filter: blur(14px);
  }

  .nav-link {
    display: none;
  }

  .nav-login {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    border-radius: 999px;
    padding: 9px 12px;
    color: #020508;
    font-size: 0.9rem;
    text-decoration: none;
    background: #f5f7fb;
  }

  .feature-grid,
  .pricing-grid,
  .steps-grid {
    grid-template-columns: 1fr;
  }

  .section,
  .hero-content {
    padding-left: 16px;
    padding-right: 16px;
  }

  .site-footer {
    flex-direction: column;
    align-items: flex-start;
    padding-left: 16px;
    padding-right: 16px;
  }

  .footer-links {
    flex-wrap: wrap;
  }
}
</style>
