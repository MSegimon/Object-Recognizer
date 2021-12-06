<?php

if (is_writable('uploads');) {
    echo "The directory is writeable";
} else {
    echo "The directory is not writeable";
}

?>