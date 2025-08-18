// [FILEPATH] src/assets/gameImages.ts
const imageModules = import.meta.glob('@/assets/game-thumbnails/*.png', { eager: true });

const imageMap: Record<string, string> = {};
for (const path in imageModules) {
  const key = path.split('/').pop()?.replace('.png', '');
  if (key) {
    // @ts-ignore
    imageMap[key] = imageModules[path].default;
  }
}

export function getGameImage(key?: string): string {
  if (key && imageMap[key]) {
    return imageMap[key];
  }
  return imageMap['default-thumbnail'] || '';
}