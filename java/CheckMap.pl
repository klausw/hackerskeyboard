#!/usr/bin/perl

binmode(STDOUT, ":utf8");

use Getopt::Std;
getopts('c') || die;

# Character used to display combining diacritics
my $placeholder = "\x{25cc}"; # dotted circle
#my $placeholder = "\x{25ab}"; # very small square
#my $placeholder = " "; # space

my %entities = (
  'amp' => '&',
  'lt' => '<',
  'gt' => '>',
);

sub expand_entity {
  my $in = shift;
  if ($entities{$in}) {
    return $entities{$in};
  }
  if ($in =~ /^#x(\w+)$/) {
    return chr(hex($1));
  } elsif ($in =~ /^#(\d+)$/) {
    return chr(0+$1);
  } else {
    return '[???]';
  }
}

sub prefix_diacritic {
  my $in = shift;
  return $in if $in =~ /^[\040-\176]/;
  #$in =~ s/^(\p{Diacritic})/$placeholder$1/;
  $in =~ s/^(\p{BidiClass:NSM})/$placeholder$1/;
  return $in;
}

my @std_map = (
	['key_tlde', 'key_ae01', 'key_ae02', 'key_ae03', 'key_ae04', 'key_ae05',
	'key_ae06', 'key_ae07', 'key_ae08', 'key_ae09', 'key_ae10', 'key_ae11',
	'key_ae12'],

	['key_ad01', 'key_ad02', 'key_ad03', 'key_ad04', 'key_ad05', 'key_ad06',
	'key_ad07', 'key_ad08', 'key_ad09', 'key_ad10', 'key_ad11', 'key_ad12',
	'key_bksl'],

	['key_ac01', 'key_ac02', 'key_ac03', 'key_ac04', 'key_ac05', 'key_ac06',
	'key_ac07', 'key_ac08', 'key_ac09', 'key_ac10', 'key_ac11'],

	['key_lsgt', 'key_ab01', 'key_ab02', 'key_ab03', 'key_ab04', 'key_ab05',
	'key_ab06', 'key_ab07', 'key_ab08', 'key_ab09', 'key_ab10'],
);
my %edge = (
  'key_ae12' => 1,
  'key_bksl' => 1,
  'key_ac11' => 1,
);
my %std_col = ();
my %std_row = ();
for (my $i = 0; $i < @std_map; ++$i) {
  my @row = @{$std_map[$i]};
  for (my $j = 0; $j < @row; ++$j) {
    my $key = $row[$j];
    $std_row{$key} = $i;
    $std_col{$key} = $j;
  }
}

sub read_strings {
  my ($href, $file) = @_;

  open(my $in, '<:utf8', $file) or die;
  while (<$in>) {
    #print;
    if (/string\s+name="(.*)"\s*>(.*)</) {
      my $name = $1;
      my $chars = $2;
      if ($chars =~ /\@string\/(\w+)/) {
        $chars = $$href{$1};
      } else {
        print STDERR "ERROR: trailing backslash for $name; " if $chars =~ /[^\\]\\$/;
        $chars =~ s/&(\#?\w+);/expand_entity($1)/eg;
        $chars =~ s/\\(.)/$1/g; # Backslashes
      }
      $$href{$name} = $chars;
    }
  }
  close $in;
}

sub main {
  my %res = ();

  foreach my $arg (@ARGV) {
    read_strings(\%res, $arg);
  }

  my %found = ();
  for (my $c = 33; $c < 127; ++$c) {
    $found{chr($c)} = 0;
  }

  my $missing_digits = '';
  my %keys = ();
  foreach my $key (sort keys %res) {
    next unless $key =~ /^key_/;
    $keys{substr($key, 0, length('key_ae01'))} = 1;

    my $chars = $res{$key};

    ## Replace entities
    #$chars =~ s/\&(\w+)\;/entities{$1}/eg;
    ## Unescape backslashes
    #$chars =~ s/\\(.)/$1/g;

    for (my $i = 0; $i < length($chars); ++$i) {
      my $char = substr($chars, $i, 1);
      #print "$key $chars: char at $i = $char\n";
      ++ $found{$char};
    }
    if ($key =~ /key_ad(\d(\d))_alt/) {
      my $keynum = $1;
      next if $keynum > 10;
      my $digit = $2;
      #print "Digit check: key=$key chars=$chars\n";
      $missing_digits .= $digit unless $chars =~ /$digit/;
    }
  }

  if ($opt_c) {
    my $prev_row = 0;
    for my $key (sort {
          $std_row{$a} <=> $std_row{$b}
            ||
          $std_col{$a} <=> $std_col{$b}
            ||
          $a cmp $b
        } keys %keys) {
      my $row = $std_row{$key};
      if ($row != $prev_row) {
        print "\n";
        $prev_row = $row;
      }
      my $main = $res{$key . '_main'};
      my $shift = $res{$key . '_shift'};
      my $alt = $res{$key . '_alt'};

      $main = prefix_diacritic($main);
      $shift = prefix_diacritic($shift);
      my @alt_list = split('', $alt);
      @alt_list = map { prefix_diacritic($_) } @alt_list;
      my $alt_join = join(' ', @alt_list);

      print "$key\t$main\t$shift\t$alt_join\n";
    }
  }

  my $missing = '';
  if ($missing_digits ne '') {
    $missing .= "Digits '$missing_digits', ";
  }

  foreach my $c (sort keys %found) {
    if ($found{$c} == 0) {
      $missing .= $c;
    }
  }
  if (length($missing) > 0) {
    print STDERR "Missing: $missing\n";
  } else {
    print STDERR "All present.\n" unless $opt_c;
  }
}

&main();
