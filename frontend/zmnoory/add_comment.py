# [FILEPATH] add_comment.py

import os
import re
from pathlib import Path

MAGIC = "[FILEPATH]"

# 경로 주석(옛날 버전)까지 잡아내기 위한 정규식
#   - 시작이 주석기호
#   - 파일 확장자 포함한 경로 조각이 들어 있음
PATH_RE = re.compile(r'[\\/].+\.(py|js|ts|tsx|vue|html|md|css|java|kt|c|cpp|h|hpp)\b', re.IGNORECASE)

COMMENT = {
    '.py':   lambda p: f"# {MAGIC} {p}",
    '.js':   lambda p: f"// {MAGIC} {p}",
    '.ts':   lambda p: f"// {MAGIC} {p}",
    '.tsx':  lambda p: f"// {MAGIC} {p}",
    '.vue':  lambda p: f"<!-- {MAGIC} {p} -->",
    '.html': lambda p: f"<!-- {MAGIC} {p} -->",
    '.md':   lambda p: f"<!-- {MAGIC} {p} -->",
    '.css':  lambda p: f"/* {MAGIC} {p} */",
    '.java': lambda p: f"// {MAGIC} {p}",
    '.kt':   lambda p: f"// {MAGIC} {p}",
    '.c':    lambda p: f"// {MAGIC} {p}",
    '.cpp':  lambda p: f"// {MAGIC} {p}",
    '.h':    lambda p: f"// {MAGIC} {p}",
    '.hpp':  lambda p: f"// {MAGIC} {p}",
}

EXCLUDE_DIRS = {'venv', '__pycache__', '.git', 'node_modules', 'dist', 'build', '.venv', 'target', 'out'}

def is_header_line(line: str) -> bool:
    s = line.strip()
    if not s:
        return False
    if not (s.startswith('#') or s.startswith('//') or s.startswith('<!--') or s.startswith('/*')):
        return False
    if MAGIC in s:
        return True
    # 예전 방식: 경로 + 확장자가 들어간 주석
    return bool(PATH_RE.search(s))

def clean_top_headers(lines: list[str]) -> list[str]:
    i = 0
    while i < len(lines) and is_header_line(lines[i]):
        i += 1
    return lines[i:]

def process_file(path: Path, root: Path):
    ext = path.suffix.lower()
    maker = COMMENT.get(ext)
    if maker is None:
        return

    rel = path.relative_to(root).as_posix()
    header = maker(rel) + "\n"

    try:
        txt = path.read_text(encoding='utf-8')
    except Exception as e:
        print(f"read err: {rel} → {e}")
        return

    lines = txt.splitlines(keepends=True)
    cleaned = clean_top_headers(lines)

    # 이미 동일 헤더가 맨 앞이면 스킵
    if cleaned and cleaned[0] == header:
        return

    new_txt = header + "".join(cleaned)
    if new_txt == txt:
        return

    try:
        path.write_text(new_txt, encoding='utf-8')
        print("written:", rel)
    except Exception as e:
        print(f"write err: {rel} → {e}")

def walk(root: Path):
    for folder, dirs, files in os.walk(root):
        # 제외 디렉터리 필터링
        dirs[:] = [d for d in dirs if d not in EXCLUDE_DIRS]
        for fn in files:
            p = Path(folder, fn)
            process_file(p, root)

if __name__ == '__main__':
    base = Path(__file__).resolve().parent
    walk(base)
