<h1 align="center">MinecraftOfflineAccountAdder</h1>

<p align="center">
  <a href="https://github.com/biscuut/MinecraftOfflineAccountAdder/releases">
    <img alt="downloads" src="https://img.shields.io/github/v/release/biscuut/MinecraftOfflineAccountAdder?color=ff5555" target="_blank" />
  </a>
    <a href="https://github.com/biscuut/MinecraftOfflineAccountAdder/releases">
      <img alt="downloads" src="https://img.shields.io/github/downloads/biscuut/MinecraftOfflineAccountAdder/total?color=ff5555" target="_blank" />
    </a>
</p>

This simple program first opens up an ugly dialog asking which account you'd like to add.

It then uses the mojang api to fix the capitalization for that username, if that username already exists (to avoid issues when joining servers). 

Finally, it will add the profile into the `launcher_profiles.json` file in your `.minecraft` folder, and select it for your minecraft launcher.