inherit image_types

IMAGE_BOOTLOADER ?= "u-boot-x1000"

# Handle u-boot suffixes
UBOOT_SUFFIX ?= "bin"
UBOOT_SUFFIX_SDCARD ?= "${UBOOT_SUFFIX}"

#BOOT components
UBOOT_SPL_POS ?= "1"
UBOOT_BIN_POS ?= "14"

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

disabled_IMAGE_CMD_sdcard () {
	if [ -z "${SDCARD_ROOTFS}" ]; then
		bberror "SDCARD_ROOTFS is undefined. To use sdcard image from Phoenix BSP it needs to be defined."
		exit 1
	fi
    
    ROOTFS_SIZE=`du -bks ${SDCARD_ROOTFS} | awk '{print $1}'`
    # Round up RootFS size to the alignment size as well
    echo "RFS size ${ROOTFS_SIZE}"
    SDIMG_SIZE=$(expr ${IMAGE_ROOTFS_ALIGNMENT} + ${ROOTFS_SIZE})

    echo "Creating filesystem with RootFS ${ROOTFS_SIZE_ALIGNED} KiB"
    echo "Creating filesystem total size ${SDIMG_SIZE} KiB"

    # Initialize sdcard image file
    echo "dd if=/dev/zero of=${SDCARD} bs=1 count=0 seek=$(expr 1024 \* ${SDIMG_SIZE})"
    dd if=/dev/zero of=${SDCARD} bs=1 count=0 seek=$(expr 1024 \* ${SDIMG_SIZE})

	# Create partition table
    parted -s ${SDCARD} mklabel msdos

    # Create rootfs partition to the end of disk
    parted -s ${SDCARD} -- unit KiB mkpart primary ext2 ${IMAGE_ROOTFS_ALIGNMENT} -1s
    parted ${SDCARD} print
    case "${IMAGE_BOOTLOADER}" in
        u-boot-x1000)
            #dd if=${DEPLOY_DIR_IMAGE}/u-boot-spl.bin of=${SDCARD} obs=512 seek=${UBOOT_SPL_POS}
            #dd if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} of=${SDCARD} obs=1k seek=${UBOOT_BIN_POS}
            # write uboot image to SPL position
            dd if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} of=${SDCARD} obs=512 seek=${UBOOT_SPL_POS}
            dd if=/dev/zero of=${SDCARD} seek=526  count=32 bs=1k
        ;;
        *)
            bberror "Unknown IMAGE_BOOTLOADER value"
            exit 1
        ;;
    esac

    # Burn Partitions
    dd if=${SDCARD_ROOTFS} of=${SDCARD} conv=notrunc seek=1 bs=$(expr ${IMAGE_ROOTFS_ALIGNMENT} \* 1024)
    /bin/sync && /bin/sync
}

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
    #parted -s ${SDCARD} mkpart data     1843200s 6037438s

    # Create rootfs partition to the end of disk
    parted ${SDCARD} print
#     case "${IMAGE_BOOTLOADER}" in
#         u-boot-x1000)
#             # TODO: write uboot image to SPL position
#             # write uboot image to SPL position
#             #dd if=${DEPLOY_DIR_IMAGE}/u-boot-spl.bin of=${SDCARD} obs=512 seek=${UBOOT_SPL_POS}
#             #dd if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} of=${SDCARD} obs=1k seek=${UBOOT_BIN_POS}
#             
#             dd if=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} of=${SDCARD} obs=512 seek=${UBOOT_SPL_POS}
#             dd if=/dev/zero of=${SDCARD} seek=526  count=32 bs=1k
#         ;;
#         *)
#             bberror "Unknown IMAGE_BOOTLOADER value"
#             exit 1
#         ;;
#     esac

    # Burn Partitions
    dd if=${SDCARD_ROOTFS} of=${SDCARD} conv=notrunc seek=409600 bs=512
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

