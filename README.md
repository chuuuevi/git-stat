# git-stat

Origin Repo: https://github.com/jhkgogpl/git-stat

# snapshot

- ![](/docs/git-stat-1.png)

- ![](/docs/git-stat-2.png)

- ![](/docs/git-stat-3.png)

# docker

```bash
docker pull ghcr.io/chuuuevi/git-stat
```

```bash
docker run --rm -it \
 -p 8080:8080 \
 -v ./:/repo \
ghcr.io/chuuuevi/git-stat:0.3.0
```
