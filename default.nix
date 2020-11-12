{ sources ? import ./nix/sources.nix
, pkgs ? import sources.nixpkgs {}
}:

pkgs.mkShell {
  buildInputs = [
    pkgs.nodejs
    pkgs.jdk
  ];

  shellHook = ''
    echo "happy hacking"
  '';
}
