param([string]$FontFile, [string]$Name)
$b = [IO.File]::ReadAllBytes($FontFile)
function U16($o) { [int]($b[$o] -shl 8 -bor $b[$o+1]) }
function U32($o) { [long]$b[$o] -shl 24 -bor [long]$b[$o+1] -shl 16 -bor [long]$b[$o+2] -shl 8 -bor [long]$b[$o+3] }
$n = U16 4
$tables = @{}
for ($i = 0; $i -lt $n; $i++) {
    $r = 12 + 16 * $i
    $tag = [Text.Encoding]::ASCII.GetString($b, $r, 4)
    $tables[$tag] = @{ off = [int](U32 ($r + 8)); len = [int](U32 ($r + 12)) }
}
$p = $tables['post'].off
$numG = U16 ($p + 32)
$idx = @(); for ($i = 0; $i -lt $numG; $i++) { $idx += U16 ($p + 34 + 2 * $i) }
$pos = $p + 34 + 2 * $numG
$custom = @()
while ($pos -lt $p + $tables['post'].len) { $l = $b[$pos]; $custom += [Text.Encoding]::ASCII.GetString($b, $pos + 1, $l); $pos += 1 + $l }
$gid = -1
for ($i = 0; $i -lt $numG; $i++) {
    $k = $idx[$i]
    $nm = if ($k -ge 258) { $custom[$k - 258] } else { '' }
    if ($nm -eq $Name) { $gid = $i; break }
}
if ($gid -lt 0) { "name not found: $Name"; exit }
$c = $tables['cmap'].off
$nsub = U16 ($c + 2)
$found = @()
for ($s = 0; $s -lt $nsub; $s++) {
    $sub = $c + [int](U32 ($c + 4 + 8 * $s))
    $fmt = U16 $sub
    if ($fmt -eq 12) {
        $ng = [int](U32 ($sub + 12))
        for ($g = 0; $g -lt $ng; $g++) {
            $e = $sub + 16 + 12 * $g
            $s0 = U32 $e; $e1 = U32 ($e + 4); $sg = U32 ($e + 8)
            for ($cp = $s0; $cp -le $e1; $cp++) { if ($sg + ($cp - $s0) -eq $gid) { $found += ('0x{0:X}' -f $cp) } }
        }
    } elseif ($fmt -eq 4) {
        $segx2 = U16 ($sub + 6); $seg = $segx2 / 2
        for ($j = 0; $j -lt $seg; $j++) {
            $end = U16 ($sub + 14 + 2*$j); $start = U16 ($sub + 16 + $segx2 + 2*$j)
            $delta = U16 ($sub + 16 + 2*$segx2 + 2*$j); $ro = U16 ($sub + 16 + 3*$segx2 + 2*$j)
            for ($cp = $start; $cp -le $end -and $cp -ne 0xFFFF; $cp++) {
                if ($ro -eq 0) { $g2 = ($cp + $delta) % 65536 }
                else { $addr = $sub + 16 + 3*$segx2 + 2*$j + $ro + 2*($cp - $start); $g2 = U16 $addr; if ($g2 -ne 0) { $g2 = ($g2 + $delta) % 65536 } }
                if ($g2 -eq $gid) { $found += ('0x{0:X}' -f $cp) }
            }
        }
    }
}
"$Name -> glyph $gid -> " + (($found | Sort-Object -Unique) -join ', ')
