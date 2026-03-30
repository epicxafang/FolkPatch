//! FPD — Android system property patching and mount-point unmounting utility.
//!
//! Supports two invocation modes:
//! 1. **argv[0] detection** — when called as `hide` or `umount` (by the apd daemon)
//! 2. **CLI flags** — `-version`, `-hide`, `-help`, `-umount`

mod prop_patch;
mod umount;

const VERSION: &str = env!("CARGO_PKG_VERSION");
const HELP_URL: &str = "https://fp.mysqil.com/";

fn print_version() {
    println!("v{VERSION}");
}

fn print_help() {
    println!("Please read the FolkPatch documentation at {HELP_URL} for help.");
}

fn usage() -> ! {
    eprintln!("Usage: fpd [-version] [-hide] [-help] [-umount]");
    std::process::exit(1);
}

fn main() {
    let args: Vec<String> = std::env::args().collect();

    // Mode 1: detect by binary name (argv[0])
    if args.len() == 1 {
        let exe = &args[0];
        let name = std::path::Path::new(exe)
            .file_name()
            .and_then(|n| n.to_str())
            .unwrap_or("");
        match name {
            "hide" => return prop_patch::run(),
            "umount" => return umount::run(),
            _ => {}
        }
    }

    // Mode 2: CLI flags (skip argv[0])
    let mut flags = args.iter().skip(1);
    let flag = flags.next();
    if flags.next().is_some() {
        usage();
    }

    match flag.map(|s| s.as_str()) {
        None => print_help(),
        Some("-version") => print_version(),
        Some("-hide") => prop_patch::run(),
        Some("-help") => print_help(),
        Some("-umount") => umount::run(),
        Some(_) => usage(),
    }
}
