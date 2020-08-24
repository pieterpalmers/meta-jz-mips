inherit image_types

#
# Create an image that can be written onto a SD card using dd.
#
# The disk layout used is:
# 	#name     =  start,   size, fstype
# 	xboot     =     0m,     3m,
# 	boot      =     3m,     8m, EMPTY
# 	recovery  =    12m,    16m, EMPTY
#         pretest   =    28m,    16m, EMPTY
#         reserved  =    44m,    52m, EMPTY
# 	misc      =    96m,     4m, EMPTY
# 	cache     =   100m,   100m, LINUX_FS
# 	system    =   200m,   700m, LINUX_FS
# 	data      =   900m,  2048m, LINUX_FS
#

IMAGE_BOOTLOADER ?= "u-boot-x1000"

# Boot partition volume id
BOOTDD_VOLUME_ID ?= "${MACHINE}"

# Set alignment to 4MB [in KiB]
IMAGE_ROOTFS_ALIGNMENT = "2048"

# Boot partition size [in KiB]
BOOT_SPACE ?= "102400"

do_image_sdcard[depends] = "\
    parted-native:do_populate_sysroot \
    mtools-native:do_populate_sysroot \
    dosfstools-native:do_populate_sysroot \
    virtual/kernel:do_deploy \
    ${IMAGE_BOOTLOADER}:do_deploy \
"

do_image_sdcard[recrdeps] = "do_build"

SDCARD = "${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.sdcard"

IMAGE_CMD_sdcard () {
	if [ -z "${SDCARD_ROOTFS}" ]; then
		bberror "SDCARD_ROOTFS is undefined. To use sdcard image from Phoenix BSP it needs to be defined."
		exit 1
	fi
    
    ROOTFS_SIZE=`du -bks ${SDCARD_ROOTFS} | awk '{print $1}'`    

    # Initialize sdcard image file
    dd if=/dev/zero of=${SDCARD} bs=512 count=0 seek=1843300

	# Create partition table
    parted -s ${SDCARD} mklabel gpt
    
    # create the table as expected by the X1000 boot
    parted -s ${SDCARD} mkpart boot        6144s   22527s
    parted -s ${SDCARD} mkpart recovery   24576s   57343s
    parted -s ${SDCARD} mkpart pretest    57344s   90111s
    parted -s ${SDCARD} mkpart reserved   90112s  196607s
    parted -s ${SDCARD} mkpart misc      196608s  204799s
    parted -s ${SDCARD} mkpart cache     204800s  409599s
    parted -s ${SDCARD} mkpart system    409600s 1843199s
    parted -s ${SDCARD} mkpart data     1843200s 100%
    parted -s ${SDCARD} print
    
    # burn bootloader to correct place
    case "${IMAGE_BOOTLOADER}" in
        u-boot-x1000)
            # write uboot image to SPL position
            # TODO size check!
            dd if=${DEPLOY_DIR_IMAGE}/u-boot-with-spl.bin of=${SDCARD} bs=512 seek=34
        ;;
        *)
            bberror "Unknown IMAGE_BOOTLOADER value"
            exit 1
        ;;
    esac

    # burn kernel
    dd if=${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} of=${SDCARD} bs=512 seek=6144
    
    # Burn root Partition
    dd if=${SDCARD_ROOTFS} of=${SDCARD} conv=notrunc seek=409600 bs=512
    
    # create an ext4 data partition
    # TODO
    
    /bin/sync && /bin/sync
}


# The sdcard requires the rootfs filesystem to be built before using
# it so we must make this dependency explicit.
IMAGE_TYPEDEP_sdcard = "ext4"

deploy_kernel () {
	rm -f ${IMAGE_ROOTFS}/boot/${KERNEL_IMAGETYPE}*
	cp ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} ${IMAGE_ROOTFS}/boot/
}

ROOTFS_POSTPROCESS_COMMAND_sdcard += " deploy_kernel ; "

