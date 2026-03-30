//! Android mount-point lazy unmounting.
//!
//! Reads a list of mount paths from `UmountPATH` (located in the parent of
//! the binary's directory) and unmounts each one using `umount2` with `MNT_DETACH`.

use std::ffi::CString;
use std::fs;
use std::path::PathBuf;

/// Lazy-unmount flag: detaches the filesystem even if it is busy.
const MNT_DETACH: libc::c_int = 2;

/// Lazy-unmount a single path via `umount2(2)`.
///
/// Returns `true` on success, `false` if the path contains a NUL byte
/// or the syscall fails.
fn umount_lazy(path: &str) -> bool {
    let c_path = match CString::new(path) {
        Ok(s) => s,
        Err(_) => return false,
    };
    // SAFETY: `c_path` is a valid NUL-terminated C string.
    unsafe { libc::umount2(c_path.as_ptr(), MNT_DETACH) == 0 }
}

/// Resolve the `UmountPATH` config file.
///
/// Binary at `/data/adb/fp/bin/fpd` → looks for `/data/adb/fp/UmountPATH`.
fn config_path() -> PathBuf {
    std::env::current_exe()
        .expect("failed to determine executable path")
        .parent()
        .expect("executable has no parent directory")
        .parent()
        .expect("executable parent has no grandparent directory")
        .join("UmountPATH")
}

/// Read `UmountPATH` and lazy-unmount each listed mount point.
pub fn run() {
    let config = config_path();
    let content = match fs::read_to_string(&config) {
        Ok(c) => c,
        Err(e) => {
            eprintln!("cannot read {}: {e}", config.display());
            return;
        }
    };

    for line in content.lines() {
        let path = line.trim();
        if path.is_empty() {
            continue;
        }
        if umount_lazy(path) {
            eprintln!("unmount ok: {path}");
        } else {
            eprintln!("unmount fail: {path}");
        }
    }
}
