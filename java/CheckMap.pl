#!/usr/bin/perl

binmode(STDOUT, ":utf8");

my %entities = (
  'amp' => '&',
  'lt' => '<',
  'gt' => '>',
);

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
        $chars =~ s/&(\w+);/$entities{$1}/eg;
        $chars =~ s/\\(.)/$1/g;
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
  foreach my $key (sort keys %res) {
    next unless $key =~ /^key_/;
    my $chars = $res{$key};
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
    print "Missing: $missing\n";
  } else {
    print "All present.\n";
  }
}

&main();
